package org.devdom.fbclient;

/**
 *
 * @author Carlos Vásquez Polanco
 */
final public class FQL {

    public static final String QUESTION_OPTIONS = "SELECT id, question_id, name, votes from question_option where question_id = :question_id order by votes desc";
    public static final String QUESTION_OPTION_VOTES = "select voter_id, option_id from question_option_votes where option_id = :option_id";
    public static final String GROUP_USERS = "select uid, first_name, last_name, name, pic_small, pic_big, pic from user where uid in (select uid from group_member where gid = :group_id)";

}