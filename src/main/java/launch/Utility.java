package launch;

public class Utility {
    public static String probeContentType(String name){
        if(name.endsWith("js")){
            return "text/javascript";
        }else if(name.endsWith("css")){
            return "text/css";

        }else if(name.endsWith("html")){
            return "text/html";
        }else{
            return "text/plain";
        }

    }
}