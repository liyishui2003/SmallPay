package Y1SHUI.common.weixin;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.Writer;
import java.util.*;

public class XmlUtil {
    public static Map<String, String> xmlToMap(HttpServletRequest request) throws Exception{
        try (InputStream inputStream = request.getInputStream()) {


            Map<String,String> map = new HashMap<>();
            SAXReader reader = new SAXReader();//SAXReader 是dom4j里的阅读器
            Document document = reader.read(inputStream);//同理Document是dom4j里用来读xml的
            Element root = document.getRootElement();
            List<Element> elements = root.elements();
            for (Element element : elements) {
                map.put(element.getName(), element.getText());
            }
            inputStream.close();//释放资源
            return map;
        }
    }

    static String mapToXml(Map<String, String> map) {
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        mapToXml2(map,sb);
        try{
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    private static void mapToXml2(Map map, StringBuffer sb) {
        Set set = map.keySet();
        for(Object o :set){
            String key = (String) o;
            Object value = map.get(key);
            if(value == null)
                value = "";
            /*
            * 递归地解析xml，微信开发里约定嵌套的形式用hashmap表示，如果有多个嵌套的hashmap并列
            * 就把这些hashmap都塞进arraylist里，所以主要解析这两种类型。
            * */
            if(value.getClass().getName().equals("java.util.ArrayList")) {
                ArrayList list = (ArrayList) value;
                sb.append("<").append(key).append(">");
                for (Object o1 : list) {
                    HashMap hm = (HashMap) o1;
                    mapToXml2(hm, sb);
                }
                sb.append("</").append(key).append(">");
            } else {
                if(value instanceof HashMap){
                    sb.append("<").append(key).append(">");
                    mapToXml2((HashMap) value, sb);
                    sb.append("</").append(key).append(">");
                } else {
                    sb.append("<").append(key).append("><![CDATA[").append(value).append("]]></").append(key).append(">");
                }
            }
        }
    }

    public static XStream getMyXStream() {
        /*
        * XStream是用来处理xml的一个库，用XppDriver来处理xml的读写
        * 如果想自定义xml的读写行为，就得自己传一个XppDriver进去，在里面重写方法
        * */
        return new XStream(new XppDriver() {
            /*
            * public HierarchicalStreamWriter createWriter(Writer out)
            * 是一个接口，用来返回一个writer
            * */
            public HierarchicalStreamWriter createWriter(Writer out) {
                /**
                 * PrettyPrintWriter 是HierarchicalStreamWriter接口的实现
                 * 是一个实现类，我们在这个类里真正重载方法
                 */
                return new PrettyPrintWriter(out) {
                    boolean cdata = true;

                    @Override
                    public void startNode(String name,Class clazz) {
                        super.startNode(name, clazz);
                    }

                    @Override
                    protected void writeText(QuickWriter writer, String text) {
                        if(cdata && !StringUtils.isNumeric(text)){
                            writer.write("<![CDATA[");
                            writer.write(text);
                            writer.write("]]>");
                        } else {
                            writer.write(text);
                        }
                    }
                };
            }
        });
    }

    public static  String beanToXml(Object bean) throws Exception {
        XStream xstream = getMyXStream();
        //将bean所在类的根节点名称改成xml，符合微信的xml格式要求
        xstream.alias("xml", bean.getClass());
        //生成注解，在类的定义中写了注解，这里用上，不然会报错
        xstream.processAnnotations(bean.getClass());
        //toXML直接调用即可
        String xml = xstream.toXML(bean);
        if(!StringUtils.isEmpty(xml)){
            return xml;
        }
        else return null;
    }

    public static <T> T xmlToBean(String resultXml,Class clazz){
        //指定DomDriver为解析器
        XStream stream = new XStream(new DomDriver());
        //允许反序列化任何类型+当前clazz(目标类型)这种
        stream.addPermission(AnyTypePermission.ANY);
        XStream.setupDefaultSecurity(stream);
        stream.allowTypes(new Class[]{clazz});
        //处理目标类中的注解
        stream.processAnnotations(new Class[]{clazz});
        //禁用对象之间的引用关系，总之是一种机制，现在不太懂先放着
        stream.setMode(XStream.NO_REFERENCES);
        //跟上面一样，指定根节点别名，确保能正确识别根节点
        stream.alias("xml",clazz);
        //将Xml字符串反序列化为Java对象
        return (T) stream.fromXML(resultXml);
    }

}
