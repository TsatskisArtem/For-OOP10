package lab10;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.*;

public class DataEditor implements Runnable {
    private static final Logger logger = LogManager.getLogger(DataEditor.class);
    private CountDownLatch latch1, latch2;

    public DataEditor(CountDownLatch latch1, CountDownLatch latch2) {
        this.latch1 = latch1;
        this.latch2 = latch2;
    }

    @Override
    public void run() {
        logger.info("Начато редактирование данных");
        try {
            latch1.await();
            File xmlFile = new File("books.xml");
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
            doc.getDocumentElement().normalize();

            Element root = doc.getDocumentElement();
            Element newBook = doc.createElement("Book");

            Element title = doc.createElement("Title");
            title.appendChild(doc.createTextNode("Новая книга"));
            newBook.appendChild(title);

            root.appendChild(newBook);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(xmlFile));

            logger.info("Данные успешно отредактированы");
            latch2.countDown();
        } catch (Exception e) {
            logger.error("Ошибка при редактировании данных", e);
        }
    }
}