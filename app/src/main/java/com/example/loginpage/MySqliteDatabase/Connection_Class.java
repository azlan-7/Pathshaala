package com.example.loginpage.MySqliteDatabase;



import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import android.util.Log;



public class Connection_Class {
    @SuppressLint("NewApi")
    public Connection CONN() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL;

        try {
            String classs = "net.sourceforge.jtds.jdbc.Driver";
            Class.forName(classs);
            Log.d("Connection_Class", "JDBC Driver Loaded Successfully");

            String ip = "199.79.62.22";
            String db = "pathshaala";
            String un = "pathshaala";
            String password = "Darshi@2025#";

            ConnURL = "jdbc:jtds:sqlserver://" + ip + ";databaseName=" + db + ";user=" + un + ";password=" + password + ";";
            conn = DriverManager.getConnection(ConnURL);

            Log.d("Connection_Class", "Connection Successful: " + conn.toString());
        } catch (ClassNotFoundException e) {
            Log.e("Connection_Class", "JDBC Driver Not Found: " + e.getMessage());
        } catch (SQLException se) {
            Log.e("Connection_Class", "SQL Exception: " + se.getMessage());
        } catch (Exception e) {
            Log.e("Connection_Class", "Unknown Exception: " + e.getMessage());
        }
        return conn;
    }
}



//public class Connection_Class {
//    //String classs="net.sourceforge.jtds.jdbc.Driver";
//    @SuppressLint("NewApi")
//    public  Connection CONN(){
//        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//        Connection conn =null;
//        String ConnURL=null;
//        try{
//            String classs="net.sourceforge.jtds.jdbc.Driver";
//            Class.forName(classs);
//            // ConnURL="jdbc:jtds:sqlserver://" + hjguyuytu76t876t87t8t78t87gjhgjhgjhgjhg7687969876868765658765gfhfjhfhjgfhffjhfjfjhfgf765757575hgfjhfjhfjhfjhgfhgfjhgfjhfjhgfjhfjhghg786786hgfhgfhjgfjhgtuyt765hgf6rfhffhgfhip+ ";databaseName=" +ahdlakdhgiuyshc87yrchfanrih8y9n8yhsfdgbc876t2876tsfgbfgangcigf8ag8fc78g8gn8g8cg37rwg7gr7w8cxblhfkbjhfnlkbnfkljnblkjnbxlkbnxclkbnkxjjdhvoi8598tyu9u3j4i309i09345i90v5i095i90i5904vvjgdfjhdlkbmo5jd9vtju98u9btuj9vtuj34vt93ujt93uj98j+bmhgksjdghjkdhgjkshrjglkhslkjhieuhyn98s4t98whseifcuhvns7yi7y45ishndkvsodh8ssssry8nt8wehtiwnehvkmteowvvvvvweioiuoiycohhkdjnhcknsu+jhgksdgh4kjscgrhbkjdfvbhituytvghfjkhgkjdfghkjdshgkjsdhgkjsdghkjsdhvkhnetehiouthjv9438tu495tu590878hgdfkljghkljghkghkjhgldkfsjghlbgdkjslgbc+lhkglsdkfhglsdkghlskdghslkdfghksdlgdjkghkjv45n749876498679769kgjdfghsdgkjsdhbkvjhivy4tiny4to8kfdjhgsjhjkdhhgdskjsdd+jkhdfskg948760376n08jkfghlkfdghdflkhgldfkghfdlkghldkfhglkdfhglkdfhglkfdhjlfkdhjlkfdiere+ ";user=" +ahdlakdhgiuyshc87yrchfanrih8y9n8yhsfdgbc876t2876tsfgbfgangcigf8ag8fc78g8gn8g8cg37rwg7gr7w8cxblhfkbjhfnlkbnfkljnblkjnbxlkbnxclkbnkxjjdhvoi8598tyu9u3j4i309i09345i90v5i095i90i5904vvjgdfjhdlkbmo5jd9vtju98u9btuj9vtuj34vt93ujt93uj98j+bmhgksjdghjkdhgjkshrjglkhslkjhieuhyn98s4t98whseifcuhvns7yi7y45ishndkvsodh8ssssry8nt8wehtiwnehvkmteowvvvvvweioiuoiycohhkdjnhcknsu+jhgksdgh4kjscgrhbkjdfvbhituytvghfjkhgkjdfghkjdshgkjsdhgkjsdghkjsdhvkhnetehiouthjv9438tu495tu590878hgdfkljghkljghkghkjhgldkfsjghlbgdkjslgbc+lhkglsdkfhglsdkghlskdghslkdfghksdlgdjkghkjv45n749876498679769kgjdfghsdgkjsdhbkvjhivy4tiny4to8kfdjhgsjhjkdhhgdskjsdd+jkhdfskg948760376n08jkfghlkfdghdflkhgldfkghfdlkghldkfhglkdfhglkdfhglkfdhjlfkdhjlkfdiere+ ";password=" +ajk4h5vk4jhkljvhjkhljkw4jh5tjkwhvj4kh5kjwhktvwhkhvkw4h5kjhkjhv45kjhkjhkvhkjh45kjhkjhvjkth45jhkjhvtk4ht5khkhv5tkhtkjhkvjh45khkjthvkjhvt4k5hkjhk4vh5khvkhvkv4hkhvk45h4hk4hkvh45kh3khkhv4k5h36khv4k5h6khkv36hk354h6kh65kvhkhkjh6k4hk345h6kh65vk4h6kj4vh6456th4ui6h4iu5h64iu56hv4iv6h4iu6hv6ih4v6h46h4vv6h4v6h4i6h4iv6h4vi+bhjgfjksdfgjksaghsgfkjsbgfjkgbjkhegrjhvg45jhg4kj5g6j4g6kjhgkvj5k2j3g5kj5gkj2g5kj4g6jh3g6g6jk2g6kj2g6k2jhg62kj5gv5jv2kg5k2jg6j6gk2j6h2g56k65k2jhg5k2j6g2kj25hj4g6kj25gk2jg5226kj2g5jk2gj2h626hj2vg52g2kj2vgjk2gkjvg6vjk26gv262g66k2g6j2kg6+cgsdkfgkg34kgghksjgfvkjgfei3gi4jkdsvbkjsvbegro4dgbkjgkesgfoi8eioehuirh384735y9384vybhjbsgv8wgtsgbfskjdgbskkkkkkkkkkgto8ajsbgjdgshvfo83tgo3kjhbdkjbvgsdjbge8tghdbgjskbvt88oh4t3qgkjbgdsjkhglkjdhv98ohihklsghdsoghv9eh8wo4ht8jdfkshvhsglshl8tyu98y398t3y9tu3jkdghkdlhfglklkdfhovgtw5tnu93ut03u98uvunue5498tuw98uy38m9u9vseoioi3oqhvnet9omooaopiumvoiatuoauopu39v3bu489ub9thnmvdoeomeemsuofiueouoiuoiruvwoiuoiwruoivugeowtu9w45t98umoiseugveow8t5q3ou98u89weutwetg9ut98miogjesoigu39wt9hjieohwj983h3w8ht3hhgidhghw4908vy98gfoidhgohvrtowhotodjgosdijgvowut49jgoisdhgvpoisgou4uggoiusehgiesht4oih5t+hjlkhsldkfgjkdghlkdghlkdfhgkdfhglkdgjdghioerugh9aerghergh9erh8shgreoahgoiahgoiuhgoaieurhfoaieughoiaheroigh985t98ththvdhgklhdglkhdlghdlakghsldghsdlghlskergoerht958t34tu98ht9hgehrgdifhohlhlgkhdlghdlgdhglkdhskgsdhfldhfshglsdlgsdhldhgklsdhgldkgh49tg84h59845u598u549859595498559hdfighdskljfghdfg985t95t895tkjdfsljfdslkhglkdlkui45thjo984htiherthaehalkhflakfhlasdhadshfldhfdhflfdfldhfldhgdhgvn948ht9343hhiougdosfoihg+hjlkhsldkfgjkdghlkdghlkdfhgkdfhglkdgjdghioerugh9aerghergh9erh8shgreoahgoiahgoiuhgoaieurhfoaieughoiaheroigh985t98ththvdhgklhdglkhdlghdlakghsldghsdlghlskergoerht958t34tu98ht9hgehrgdifhohlhlgkhdlghdlgdhglkdhskgsdhfldhfshglsdlgsdhldhgklsdhgldkgh49tg84h59845u598u549859595498559hdfighdskljfghdfg985t95t895tkjdfsljfdslkhglkdlkui45thjo984htiherthaehalkhflakfhlasdhadshfldhfdhflfdfldhfldhgdhgvn948ht9343hhiougdosfoihg+hlvhlkdfhvkldshgldghldhglsdhgldfhglkdghlkhglkdghlksdhgldshglfdhgldhsfglkdshfgldhgldfhglsdghldghdslfsdlghdlfghfdlsghsdlghdlsghldghdlghdslghdlghsdlghlsdghdlsghdlghdslgdglkdjsh95t8u49gkjlfdghlkdhg98985e+ ";";
//            //conn =DriverManager.getConnection(ConnURL);
//
//
//
//
//            String ip = "199.79.62.22";
//            String db = "pathshaala";
//            String un = "pathshaala";
//            String password = "Darshi@2025#";
//            ConnURL="jdbc:jtds:sqlserver://" + ip + ";databaseName=" + db + ";user=" + un + ";password=" + password + ";";
//            conn =DriverManager.getConnection(ConnURL);
//            Log.d("Connection_Class", "Value of" + "  s0 =" + conn.toString());
//
//
//            // ConnURL="jdbc:jtds:sqlserver://" + hjguyuytu76t876t87t8t78t87gjhgjhgjhgjhg7687969876868765658765gfhfjhfhjgfhffjhfjfjhfgf765757575hgfjhfjhfjhfjhgfhgfjhgfjhfjhgfjhfjhghg786786hgfhgfhjgfjhgtuyt765hgf6rfhffhgfhip+ ";databaseName=" +ahdlakdhgiuyshc87yrchfanrih8y9n8yhsfdgbc876t2876tsfgbfgangcigf8ag8fc78g8gn8g8cg37rwg7gr7w8cxblhfkbjhfnlkbnfkljnblkjnbxlkbnxclkbnkxjjdhvoi8598tyu9u3j4i309i09345i90v5i095i90i5904vvjgdfjhdlkbmo5jd9vtju98u9btuj9vtuj34vt93ujt93uj98j+bmhgksjdghjkdhgjkshrjglkhslkjhieuhyn98s4t98whseifcuhvns7yi7y45ishndkvsodh8ssssry8nt8wehtiwnehvkmteowvvvvvweioiuoiycohhkdjnhcknsu+jhgksdgh4kjscgrhbkjdfvbhituytvghfjkhgkjdfghkjdshgkjsdhgkjsdghkjsdhvkhnetehiouthjv9438tu495tu590878hgdfkljghkljghkghkjhgldkfsjghlbgdkjslgbc+lhkglsdkfhglsdkghlskdghslkdfghksdlgdjkghkjv45n749876498679769kgjdfghsdgkjsdhbkvjhivy4tiny4to8kfdjhgsjhjkdhhgdskjsdd+jkhdfskg948760376n08jkfghlkfdghdflkhgldfkghfdlkghldkfhglkdfhglkdfhglkfdhjlfkdhjlkfdiere+ ";user=" +ahdlakdhgiuyshc87yrchfanrih8y9n8yhsfdgbc876t2876tsfgbfgangcigf8ag8fc78g8gn8g8cg37rwg7gr7w8cxblhfkbjhfnlkbnfkljnblkjnbxlkbnxclkbnkxjjdhvoi8598tyu9u3j4i309i09345i90v5i095i90i5904vvjgdfjhdlkbmo5jd9vtju98u9btuj9vtuj34vt93ujt93uj98j+bmhgksjdghjkdhgjkshrjglkhslkjhieuhyn98s4t98whseifcuhvns7yi7y45ishndkvsodh8ssssry8nt8wehtiwnehvkmteowvvvvvweioiuoiycohhkdjnhcknsu+jhgksdgh4kjscgrhbkjdfvbhituytvghfjkhgkjdfghkjdshgkjsdhgkjsdghkjsdhvkhnetehiouthjv9438tu495tu590878hgdfkljghkljghkghkjhgldkfsjghlbgdkjslgbc+lhkglsdkfhglsdkghlskdghslkdfghksdlgdjkghkjv45n749876498679769kgjdfghsdgkjsdhbkvjhivy4tiny4to8kfdjhgsjhjkdhhgdskjsdd+jkhdfskg948760376n08jkfghlkfdghdflkhgldfkghfdlkghldkfhglkdfhglkdfhglkfdhjlfkdhjlkfdiere+ ";password=";";
//            //conn =DriverManager.getConnection(ConnURL);
//
//        }
//       /* catch (SQLException se){
//
//            Log.e("ERROR", se.getMessage());
//        }*/
//        catch (ClassNotFoundException e){
//            Log.e("ERROR", e.getMessage());
//        }
//        catch (Exception e){
//            Log.e("ERROR",e.getMessage());
//        }
//        return  conn;
//    }
//
//}
