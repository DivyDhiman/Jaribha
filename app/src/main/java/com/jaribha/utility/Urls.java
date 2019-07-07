package com.jaribha.utility;

/**
 * Created by Kailash Chouhan (Ratufa Technologies)
 * Email: kailash.chouhan@ratufa.com
 */
public interface Urls {

    // API KEY
    String API_KEY = "1a2b3c4d5e";

    // Base Url
    //String BASE = "http://jaribha.inventmedia.info/jaribha/"; // live
    //String BASE = "http://staging.jaribha.com/jaribha/"; // New live
    String BASE = "http://api.jaribha.com/v1/jaribha/"; // New live 22.09.2016

    //http://staging.jaribha.com/jaribha/
    // instead of
    // http://jaribha.inventmedia.info/jaribha/ link.

    String JUSERS = "jusers/";

    String JOTHERS = "jothers/";

    String JFILTERS = "jfilters/";

    String JCOMMUNITIES = "jcommunities/";

    String JPROJECT = "jproject/";

    String JCATEGORY = "jcategories/";

    String JREWARDS = "jrewards/";

    String JSPONSORS = "jsponsors/";

    String JPAYMENT = "jpayment/";

    // For Login
    String LOGIN = BASE + JUSERS + "login.json";

    // For Sign Up
    String SIGN_UP = BASE + JUSERS + "signup.json";

    // For Get user info
    String GET_USER_INFO = BASE + JUSERS + "getuser.json";

    // For update profile
    String UPDATE_PROFILE = BASE + JUSERS + "update_profile.json";

    // for get User City
    String GET_CITY = BASE + JOTHERS + "get_cities.json";

    // for get projects
    String GET_PROJECTS = BASE + JFILTERS + "get_projects.json";

    // for get projects
    String GET_PROJECTS_BY_COMMUNITY = BASE + JFILTERS + "get_project_by_community.json";

    //For get communities
    String GET_COMMUNITIES = BASE + JCOMMUNITIES + "get_communities.json";

    // get_community_names
    String GET_COMMUNITIES_NAMES = BASE + JCOMMUNITIES + "get_community_names.json";

    //For add communities
    String ADD_COMMUNITIES = BASE + JCOMMUNITIES + "add_community.json";

    // http://jaribha.inventmedia.info/jaribha/jproject/add_project.json
    String ADD_PROJECT = BASE + JPROJECT + "add_project.json";

    // http://jaribha.inventmedia.info/jaribha/jcategories/get_categories.json
    String GET_CATEGORY = BASE + JCATEGORY + "get_categories.json";

    // http://jaribha.inventmedia.info/jaribha/jothers/get_cities_by_country_v1.json
    String GET_CITIES_BY_COUNTRIES_V1 = BASE + JOTHERS + "get_cities_by_country_v1.json";

    // http://jaribha.inventmedia.info/jaribha/jothers/get_cities_by_country.json
    String GET_CITIES_BY_COUNTRIES = BASE + JOTHERS + "get_cities_by_country.json";

    // http://jaribha.inventmedia.info/jaribha/jothers/get_countries.json
    String GET_COUNTRIES = BASE + JOTHERS + "get_countries.json";

    String GET_COUNTRIES_BY_PROJECT = BASE + JOTHERS + "get_countries_by_projects.json";

    // For Forget Password
    String FORGET_PASSWORD = BASE + JUSERS + "forget_password.json";

    // For Ask Question
    String ASK_QUESTION = BASE + JOTHERS + "ask_question_v1.json";

    // http://jaribha.inventmedia.info/jaribha/jproject/add_stories.json
    String ADD_STORIES = BASE + JPROJECT + "add_stories.json";

    // http://jaribha.inventmedia.info/jaribha/jproject/add_userabout.json
    String ADD_USER_ABOUT = BASE + JPROJECT + "add_userabout.json";

    // For Is Social Exist or Not
    String IS_SOCIAL_EXIST = BASE + JUSERS + "is_social_exist.json";

    // For My Portfolio
    String MY_PORTFOLIO = BASE + JUSERS + "my_portfolio.json";

    // For Get Messages
    String GET_MESSAGES = BASE + JOTHERS + "get_messages_v1.json";

    // For Get Messages History
    String GET_MESSAGES_HISTORY = BASE + JOTHERS + "get_messages_history.json";

    // For Get Messages History
    String GET_PROJECT_DETAIL = BASE + JFILTERS + "get_project_detail.json";

    // For Get Rewards
    String GET_PROJECT_REWARDS = BASE + JREWARDS + "get_rewards.json";

    // For Get Comments
    String GET_PROJECT_COMMENTS = BASE + JPROJECT + "get_comments.json";

    // For Report Project
    String REPORT_PROJECT = BASE + JPROJECT + "reportspam.json";

    // For Add Comments
    String ADD_COMMENTS = BASE + JPROJECT + "add_comment.json";

    // For Update Project
    String UPDATE_PROJECT = BASE + JPROJECT + "update_project.json";

    // For Change Password
    String CHANGE_PASSWORD = BASE + JUSERS + "change_password.json";

    // For Update language
    String UPDATE_LANGUAGE = BASE + JUSERS + "update_language.json";

    // For Update Notification
    String UPDATE_EMAIL_SETTINGS = BASE + JUSERS + "update_notification.json";

    String UPDATE_PUSH_SETTINGS = BASE + JUSERS + "update_push_notifications.json";

    // http://jaribha.inventmedia.info/jaribha/jproject/add_account.json
    String ADD_ACCOUNT = BASE + JPROJECT + "add_account.json";

    // http://jaribha.inventmedia.info/jaribha/jproject/add_reward.json
    String ADD_REWARDS = BASE + JPROJECT + "add_reward.json";

    // For Project History
    String PROJECT_HISTORY = BASE + JPROJECT + "project_history.json";

    // For Contact Us
    String CONTACT_US = BASE + JOTHERS + "contact_us.json";

    // For Become Sponsor
    String BECOME_SPONSOR = BASE + JPROJECT + "becomeasponsor.json";

    // For Delete Project
    String DELETE_PROJECT = BASE + JPROJECT + "delete_project.json";

    // http://jaribha.inventmedia.info/jaribha/jproject/add_favourites.json
    String ADD_FAVOURITE = BASE + JPROJECT + "add_favourites.json";


    String GET_SPONSORS_BY_PROJECT = BASE + JSPONSORS + "get_sponsors_by_project.json";


    String GET_SPONSORS_INFO_BY_ID = BASE + JSPONSORS + "get_sponsor_by_id.json";


    String GET_PROJECT_UPDATES = BASE + JPROJECT + "get_project_updates.json";

    // For Get Sponsors
    String GET_SPONSORS = BASE + JSPONSORS + "get_sponsors.json";

    // http://jaribha.inventmedia.info/jaribha/jproject/delete_reward.json
    String DELETE_REWARD = BASE + JPROJECT + "delete_rewards.json";

    // http://jaribha.inventmedia.info/jaribha/jproject/review_project.json
    String REVIEW_PROJECT = BASE + JPROJECT + "review_project.json";

    // For News Letter
    String NEWS_LETTER = BASE + JOTHERS + "newsletter.json";

    // Get Favourites
    String GET_FAVOURITES = BASE + JPROJECT + "get_favourites.json";


    String GET_PROJECT_SUPPORTERS = BASE + JSPONSORS + "get_supporter_by_project.json";

    String GET_PORTFOLIO_BY_ID = BASE + JUSERS + "user_portfolio_by_id.json";

    String GET_MY_HISTORY = BASE + JPROJECT + "get_sponsor_supporter_by_id.json";

    // http://jaribha.inventmedia.info/jaribha/jproject/submit_project.json
    String SUBMIT_PROJECT = BASE + JPROJECT + "submit_project.json";

    // http://jaribha.inventmedia.info/jaribha/jpayment/jknetPayment.json
    String KNET_PAYMENT = BASE + JPAYMENT + "jknetPayment.json";

    // http://jaribha.inventmedia.info/jaribha/jpayment/jcreditcardPayment.json
    String CREDIT_CARD_PAYMENT = BASE + JPAYMENT + "jcreditcardPayment.json";

    String LOG_OUT = BASE + JUSERS + "logout.json";

    String SEND_MESSAGE = BASE + JOTHERS + "send_message_v1.json";

    // http://jaribha.inventmedia.info/jaribha/jpayment/usd_to_kwd.json
    String USD_TO_KWD = BASE + JPAYMENT + "usd_to_kwd.json";

    // http://jaribha.inventmedia.info/jaribha/jfilters/get_incomplete_project_detail.json
    String GET_INCOMPLETE_PROJECT_DETAILS = BASE + JFILTERS + "get_incomplete_project_detail.json";

    String GET_MSG_FAV_COUNT = BASE + JUSERS + "get_user_msgfavcount.json";

    String UPDATE_MESSAGE_STATUS = BASE + JOTHERS + "update_message_status.json";

    String GET_TERMS = BASE + JOTHERS + "get_terms.json";

    String GET_POLICY = BASE + JOTHERS + "get_policy.json";

    String GET_GUIDELINES = BASE + JOTHERS + "get_guidelines.json";

    String BASE_URL_abarb = "http://jaribha.inventmedia.info/jaribha/jpages/htmlpage?language=arab&pagename="; // New live 22.09.2016

    String BASE_URL_eng = "http://jaribha.inventmedia.info/jaribha/jpages/htmlpage?language=eng&pagename="; // New live 22.09.2016

}
