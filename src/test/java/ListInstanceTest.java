import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;

import java.util.ArrayList;
import java.util.List;

public class ListInstanceTest {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList();
        List<Double> list2 = new ArrayList();
        Object listObject = list2;

        System.out.println(listObject instanceof List);
    }

}
