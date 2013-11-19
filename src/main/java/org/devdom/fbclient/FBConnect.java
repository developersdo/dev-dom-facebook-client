package org.devdom.fbclient;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.conf.ConfigurationBuilder;
import facebook4j.internal.org.json.JSONArray;
import facebook4j.internal.org.json.JSONException;
import facebook4j.internal.org.json.JSONObject;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.devdom.fbclient.model.dto.Skillset;
import org.devdom.fbclient.model.dto.Users;
import org.devdom.fbclient.model.dto.Votes;
import org.devdom.fbclient.model.dto.VotesPK;

/**
 *
 * @author Carlos Vasquez Polanco
 */
public class FBConnect {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa");
    private final ConfigurationBuilder cb = Configuration.getDoConfig();
    private FacebookFactory ff = null;
    
    private String questionID;
    private String groupID;
    
    public FBConnect(){
        ff = new FacebookFactory(cb.build());
    }

    public EntityManager getEntityManager(){
        return emf.createEntityManager();
    }
    

    private Facebook connect(){
        if(ff==null) 
            ff = new FacebookFactory();
        return ff.getInstance();
    }

    private JSONArray getFBDevelopers() {
        String query = FQL.GROUP_USERS;
        query = query.replace(":group_id",groupID);
        try {
            return connect().executeFQL(query);
        } catch (FacebookException ex) {
            Logger.getLogger(FBConnect.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private JSONArray getFBVoters(Long skillID) {
        String query = FQL.QUESTION_OPTION_VOTES;
        query = query.replaceAll(":option_id", skillID.toString());
        try {
            return connect().executeFQL(query);
        } catch (FacebookException ex) {
            Logger.getLogger(FBConnect.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public JSONArray getSkills(){
        try {
            String query = FQL.QUESTION_OPTIONS;
            query = query.replace(":question_id",questionID);
            return connect().executeFQL(query);
        } catch (FacebookException ex) {
            Logger.getLogger(FBConnect.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public int totalFBVotes(){
        int count = 0;
        JSONArray result = getSkills();
        try {
            for (int i = 0; i < result.length(); i++){
                JSONObject json = result.getJSONObject(i); 
                count += json.getInt("votes");
            }
        } catch (JSONException ex) {
            Logger.getLogger(FBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        return count;
    }

    private List<VotesPK> getDBVoters(Long skillId){
        return getEntityManager().createNamedQuery("Votes.findByFbOptionId")
                                 .setParameter("fbOptionId",skillId)
                                 .getResultList();
    }

    public int updateVotes(Long skillId){
        EntityManager em = getEntityManager();
        int count = 0;

        JSONArray result = getFBVoters(skillId);
        List<VotesPK> votes = getDBVoters(skillId);

        if(result.length() != votes.size()){
            em.getTransaction().begin();
            em.createNamedQuery("Votes.delByFBOptionId").
                    setParameter("fbOptionId",skillId)
                    .executeUpdate();
            
            for (int i = 0; i < result.length(); i++){
                try {
                    JSONObject json = result.getJSONObject(i);
                    Votes newVote = new Votes(skillId,json.getLong("voter_id"));
                    em.persist(newVote);
                    count++;
                } catch (JSONException ex) {
                    em.getTransaction().rollback();
                    Logger.getLogger(FBConnect.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            em.getTransaction().commit();
        }
        return count;
    }
    
    public void updateDevelopers(){

        EntityManager em = getEntityManager();
        List<Users> developers = em.createNamedQuery("Users.findAll").getResultList();
        JSONArray result = getFBDevelopers();
        int updated = 0;
        int added = 0;
        em.getTransaction().begin();
        try {
            boolean devExists;
            for(int i=0;i<result.length();i++){
                devExists = false;
                JSONObject json = result.getJSONObject(i);
                for(Users developer : developers){                    
                    if(json.getString("uid").equals(developer.getUid())){
                        devExists = true;
                        developer.setUid(json.getString("uid"));
                        developer.setFirstName(json.getString("first_name"));
                        developer.setLastName(json.getString("last_name"));
                        developer.setPicSmall(json.getString("pic_small"));
                        developer.setPicBig(json.getString("pic_big"));
                        developer.setPic(json.getString("pic"));
                        Logger.getLogger(FBConnect.class.getName()).log(Level.INFO, "actualizando developer => {0} {1}", new Object[]{json.getString("first_name"), json.getString("last_name")});
                        updated++;
                    }
                }
                if(!devExists){
                    Users newDev = new Users();
                    newDev.setUid(json.getString("uid"));
                    newDev.setFirstName(json.getString("first_name"));
                    newDev.setLastName(json.getString("last_name"));
                    newDev.setPicSmall(json.getString("pic_small"));
                    newDev.setPicBig(json.getString("pic_big"));
                    newDev.setPic(json.getString("pic"));
                    em.persist(newDev);
                    Logger.getLogger(FBConnect.class.getName()).log(Level.INFO, "nuevo developer => {0} {1}", new Object[]{json.getString("first_name"), json.getString("last_name")});
                    added++;
                }
            }
        } catch (JSONException ex) {
            Logger.getLogger(FBConnect.class.getName()).log(Level.SEVERE, null, ex);
            em.getTransaction().rollback();
        }
        Logger.getLogger(FBConnect.class.getName()).log(Level.INFO, "fueron agreados {0} y actualidos {1} developers", new Object[]{added, updated});
        em.getTransaction().commit();
    }

    public void updateSkills(){

        EntityManager em = getEntityManager();
        List<Skillset> skills = em.createNamedQuery("Skillset.findAll").getResultList();
        
        int updated = 0;
        JSONArray result = getSkills();
        for(Skillset skill : skills){
            for (int i = 0; i < result.length(); i++){
                try {
                    em.getTransaction().begin();
                    JSONObject json = result.getJSONObject(i);
                    if( json.get("id").equals(skill.getOptionId()) ){
                        if(!json.get("votes").equals(skill.getVotes())){
                            updated++;
                            Logger.getLogger(FBConnect.class.getName()).log(Level.INFO, "skill : {0}", json.getString("name"));
                            Logger.getLogger(FBConnect.class.getName()).log(Level.INFO, "cambiaron los votos FB {0}, DB {1}", new Object[]{json.getInt("votes"), skill.getVotes()});
                            skill.setVotes(json.getInt("votes"));

                            int count = updateVotes(json.getLong("id"));
                            if(count>0){
                                Logger.getLogger(FBConnect.class.getName()).log(Level.INFO, "fueron actualizados {0} developers con el skill {1}", new Object[]{count, json.get("name")});
                            }
                        }
                    }
                    em.getTransaction().commit();
                } catch (JSONException ex) {
                    Logger.getLogger(FBConnect.class.getName()).log(Level.SEVERE, null, ex);
                }finally{
                    if(em.getTransaction().isActive()){
                        em.getTransaction().rollback();
                    }
                }
            }
        }
        Logger.getLogger(FBConnect.class.getName()).log(Level.INFO, "skill actualizados: {0}", updated);
    }

    /**
     * @return the questionID
     */
    public String getQuestionID() {
        return questionID;
    }

    /**
     * @param questionID the questionID to set
     */
    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    /**
     * @return the groupID
     */
    public String getGroupID() {
        return groupID;
    }

    /**
     * @param groupID the groupID to set
     */
    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

}