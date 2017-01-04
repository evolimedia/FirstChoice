package firstchoice.technopear.com.firstchoice.common;

import org.ksoap2.serialization.SoapPrimitive;

/*
 * Created by sanjay on 12/7/16.
 */
public class Urls {
    public final static String Main_URL = "http://technopear.com/firstchoice/soap.php?wsdl";
    public static final String result = "result";

    private final static String urn = "urn:firstchoiceapi#";
    public static String SOAP_ACTION_CREATE_ACCOUNT = urn + "create_account";
    public static String SOAP_ACTION_GET_DATA = urn + "get_data";
    public static String SOAP_ACTION_UPDATE_DATA = "urn:firstchoiceapi#update_Data";
    public static String SOAP_ACTION_REMOVE_DATA = urn + "remove_Data";

    public static String SOAP_ACTION_GENERATE_ALIAS_ID = urn + "generate_dndaliasid";


    public static String NAMESPACE = "urn:firstchoiceapi";
    public static SoapPrimitive resultString;

    public static int TimeOut = 70000;

}
