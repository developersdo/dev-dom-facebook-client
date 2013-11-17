package org.devdom.fbclient.model;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Carlos Vásquez Polanco
 */
@Entity
@Table(name = "skillset_option_vote")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Votes.findAll", query = "SELECT v FROM Votes v"),
    @NamedQuery(name = "Votes.findByFbOptionId", query = "SELECT v FROM Votes v WHERE v.votesPK.fbOptionId = :fbOptionId"),
    @NamedQuery(name = "Votes.findByFbUid", query = "SELECT v FROM Votes v WHERE v.votesPK.fbUid = :fbUid")})
public class Votes implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected VotesPK votesPK;

    public Votes() {
    }

    public Votes(VotesPK votesPK) {
        this.votesPK = votesPK;
    }

    public Votes(long fbOptionId, long fbUid) {
        this.votesPK = new VotesPK(fbOptionId, fbUid);
    }

    public VotesPK getVotesPK() {
        return votesPK;
    }

    public void setVotesPK(VotesPK votesPK) {
        this.votesPK = votesPK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (votesPK != null ? votesPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Votes)) {
            return false;
        }
        Votes other = (Votes) object;
        if ((this.votesPK == null && other.votesPK != null) || (this.votesPK != null && !this.votesPK.equals(other.votesPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.devdom.fbclient.model.Votes[ votesPK=" + votesPK + " ]";
    }
    
}
