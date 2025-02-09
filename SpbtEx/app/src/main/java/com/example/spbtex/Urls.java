package com.example.spbtex;

public class Urls {

    public static final String SERVER = "http://10.0.2.2:8080/"; //"http://10.0.2.2:8080/"
    public static final String CONTEXT_PATH = "aws-sample/";
    public static final String ANDROID_REQUEST = "android/";

    public static final String URL_LOGIN_POST = SERVER + CONTEXT_PATH + ANDROID_REQUEST + "login";
    public static final String URL_MAIL_REGISTER_POST = SERVER + CONTEXT_PATH + ANDROID_REQUEST + "mail_register";
    public static final String URL_CODE_REGISTER_POST = SERVER + CONTEXT_PATH + ANDROID_REQUEST + "code_register";
    public static final String URL_MEMBER_REGISTER_POST = SERVER + CONTEXT_PATH + ANDROID_REQUEST + "form_register";
    public static final String URL_CHECK_SESSION_POST = SERVER + CONTEXT_PATH + ANDROID_REQUEST + "checkSession";
    public static final String URL_DELETE_SESSION_POST = SERVER + CONTEXT_PATH + ANDROID_REQUEST + "deleteSession";
    public static final String URL_FACILITIES_GET = SERVER + CONTEXT_PATH + ANDROID_REQUEST + "facility";
    public static final String URL_CALENDER_GET = SERVER + CONTEXT_PATH + ANDROID_REQUEST + "calendarDays";
    public static final String URL_SELECT_DAY_POST = SERVER + CONTEXT_PATH + ANDROID_REQUEST + "reservation/select_day";
    public static final String URL_RESERVATION_POST = SERVER + CONTEXT_PATH + ANDROID_REQUEST + "reservation/create";
    public static final String URL_MEMBER_HOME_POST = SERVER + CONTEXT_PATH + ANDROID_REQUEST + "member_show";
    public static final String URL_REMAIN_POST = SERVER + CONTEXT_PATH + ANDROID_REQUEST + "remainList";
    public static final String URL_PAST_POST = SERVER + CONTEXT_PATH + ANDROID_REQUEST + "pastList";
    public static final String URL_R_CANCEL_POST = SERVER + CONTEXT_PATH + ANDROID_REQUEST + "r_cancel";
    public static final String URL_UPDATE_MAIL_POST = SERVER + CONTEXT_PATH + ANDROID_REQUEST + "update_mail";
    public static final String URL_UPDATE_MAIL_CODE_POST = SERVER + CONTEXT_PATH + ANDROID_REQUEST + "update_mail_code_register";
    public static final String URL_UPDATE_PASS_POST = SERVER + CONTEXT_PATH + ANDROID_REQUEST + "update_pass";
    public static final String URL_UPDATE_ETC_POST = SERVER + CONTEXT_PATH + ANDROID_REQUEST + "update_etc";
    public static final String URL_DELETE_MEMBER_POST = SERVER + CONTEXT_PATH + ANDROID_REQUEST + "delete_member";
    public static final String URL_ATTACHED_FILE_GET = SERVER + CONTEXT_PATH + ANDROID_REQUEST + "load_attached_files";
    public static final String URL_DOWNLOAD_IMAGE_POST = SERVER + CONTEXT_PATH + ANDROID_REQUEST + "download_image";
}
