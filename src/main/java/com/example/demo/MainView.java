package com.example.demo;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


@Route
public class MainView extends VerticalLayout
{

    private String url="";
    private String response;
    private String wal_c;
    private String rate_s;
    private double rate_d;
    private org.w3c.dom.Document document;
    private String[] str={"USD","CHF","THB","AUD","HKD","CAD","CZK","EUR","GBP"};
    private Button bt2=new Button("Przelicz");
    private Text tx=new Text("Przelicznik Walut");
    private Text err=new Text("");
    private Text kurs=new Text("Aktualny kurs: ");
    private Text wal=new Text("");
    private TextField tf=new TextField();
    private Text tx2 =new Text("");
    private ComboBox cb=new ComboBox();
    class MyClickListener implements ComponentEventListener<ClickEvent<Button>>
    {
        @Override
        public void onComponentEvent(ClickEvent<Button> event)
        {
            String content;
            wal_c=cb.getValue().toString();
            url=String.format("http://api.nbp.pl/api/exchangerates/rates/a/%s/?format=xml",wal_c);
            content=getURLContent(url);
            rate_s=parseXmlFromString(content);
            wal.setText(rate_s);
            if(tf.getValue()=="")
                err.setText("Podaj wartość");
            else
                err.setText("");
                przelicz();
        }
    }
    public MainView()
    {
        cb.setItems(str);
        cb.setPlaceholder("Wybierz walutę");
        VerticalLayout vl=new VerticalLayout(tx,tf,cb,kurs,wal);
        VerticalLayout vl2=new VerticalLayout(tx2,bt2,err);
        tf.setRequired(true);
        tf.setLabel("Podaj ilość");
        add(vl);
        add(vl2);
        vl.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        vl2.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        bt2.addClickListener(new MyClickListener());
    }
    public String getURLContent(String p_sURL)
    {
        URL oURL;
        URLConnection oConnection;
        BufferedReader oReader;
        String sLine;
        StringBuilder sbResponse;
        try
        {
            oURL = new URL(p_sURL);
            oConnection = oURL.openConnection();
            oReader = new BufferedReader(new InputStreamReader(oConnection.getInputStream()));
            sbResponse = new StringBuilder();

            while((sLine = oReader.readLine()) != null)
            {
                sbResponse.append(sLine);
            }

            response = sbResponse.toString();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return response;
    }
    public String parseXmlFromString(String xmlString){
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream inputStream = new ByteArrayInputStream(xmlString.getBytes());
            document = builder.parse(inputStream);
            XPath xPath = XPathFactory.newInstance().newXPath();
            Node node = (Node) xPath.evaluate("/ExchangeRatesSeries/Rates/Rate/Mid/text()", document, XPathConstants.NODE);
            return node.getNodeValue();
        }catch (Exception e) {
            err.setText("Brak danych z dzisiejszego dnia");
        }
        return null;
    }
    public void przelicz()
    {
        double val;
        double end;
        val=Double.parseDouble(tf.getValue());
        rate_d=Double.parseDouble(rate_s);
        end=rate_d*val;
        tx2.setText(String.valueOf(end)+ " PLN");
    }
}
