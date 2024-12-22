package lab10;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.*;

public class DataLoader implements Runnable {
    private static final Logger logger = LogManager.getLogger(DataLoader.class);
    private CountDownLatch latch;

    public DataLoader(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void run() {
        logger.info("Начата загрузка данных из XML");
        try {
            File xmlFile = new File("books.xml");
            if (!xmlFile.exists()) {
                logger.warn("Файл XML не найден");
                latch.countDown();
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);

            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("Book");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    logger.debug("Загружена книга: " + element.getElementsByTagName("Title").item(0).getTextContent());
                }
            }
            latch.countDown();
        } catch (Exception e) {
            logger.error("Ошибка при загрузке данных", e);
        }
    }
}