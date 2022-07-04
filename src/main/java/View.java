import spark.Request;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class View {
    private Request request;

    public View(Request request) {
        this.request = request;
    }

    public String index() {
        return this.configDefault(this.renderContent("index.html"));
    }

    public String response() {
        return this.configDefault(this.renderContent("resultado.html"));
    }

    private String configDefault(String content) {
        return content.replace("{{URLBASE}}", this.request.scheme() +"://"+ this.request.host());
    }

    private String renderContent(String htmlFile) {
        try {
            URL url = getClass().getResource("public/" + htmlFile);
            Path path = Paths.get(url.toURI());
            return new String(Files.readAllBytes(path), Charset.defaultCharset());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
