package org.devdom.fbclient;

/**
 *
 * @author Carlos Vasquez Polanco
 */
public class App 
{
    
    public static void main( String[] args )
    {
        FBConnect fbConnect = new FBConnect();
        fbConnect.setGroupID("201514949865358");
        fbConnect.setQuestionID("323442071005978"); //Cuales son tus skillls?
        fbConnect.updateSkills();
        fbConnect.setQuestionID("562574913759358"); //Cuales son tus skillls? (Plataforma para la que desarrollas)
        fbConnect.updateSkills();
        fbConnect.setQuestionID("558308817519301"); //Cuales son tus skillls? (Manejadores de versión)
        fbConnect.updateSkills();
        fbConnect.setQuestionID("606824229334426"); //Universidades
        fbConnect.updateSkills();
        fbConnect.updateUniversities();
        fbConnect.updateDevelopers();
    }
}
