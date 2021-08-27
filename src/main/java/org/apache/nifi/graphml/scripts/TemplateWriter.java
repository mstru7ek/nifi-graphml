package org.apache.nifi.graphml.scripts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.LinkedList;

public class TemplateWriter {
    private static final String PROCESSOR_TEMPLATE_XML = "templates/processor.xml";
    private static final String TEMPLATES_GRAPH_XML = "templates/graph.xml";

    private static final double WIDTH = 350.00f;
    private static long id = 0;
    private static byte[] bytes = null;
    private static double accumulatedY = 0.0f;

    public static void writeLead(OutputStream outputStream) {
        try {
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(TEMPLATES_GRAPH_XML);
            inputStream.transferTo(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeClose(OutputStream outputStream) throws IOException {
        String closing = "</graph></graphml>";
        outputStream.write(closing.getBytes());
    }

    public static void writeProcessor(String title, String[] descriptionText, String[] tagsList, OutputStream outputStream) throws IOException, URISyntaxException {
        if (bytes == null) {
            bytes = Thread.currentThread().getContextClassLoader().getResourceAsStream(PROCESSOR_TEMPLATE_XML).readAllBytes();
        }
        String template = new String(bytes);

        final double pheight = Math.max(300.0, (descriptionText.length + 1) * 28.0);
        final String description = String.join("\n\n", descriptionText);

        final double x = 0.0;
        final double y = accumulatedY + 30.0;

        // template window
        template = template.replace("%processorId%", "n" + id++);
        template = template.replace("%processorKey%", "d6");
        template = template.replaceAll("%pheight%", Double.toString(pheight));
        template = template.replaceAll("%pwidth%", Double.toString(WIDTH));
        template = template.replace("%px%", Double.toString(x));
        template = template.replace("%py", Double.toString(y));

        template = template.replace("%ptitle%", title);
        template = template.replace("%plx%", "150.0");
        template = template.replace("%pdescription%", description);

        // tag window
        double tY = y + pheight + 15.0;

        final double theight = (tagsList.length) * 18.00 + 2*4.00;
        final String tagContent = String.join("\n", tagsList);

        template = template.replace("%tagId%", "n" + id++);
        template = template.replace("%tagKey%", "d6");
        template = template.replace("%theight%", Double.toString(theight));
        template = template.replaceAll("%twidth%", Double.toString(WIDTH));
        template = template.replace("%tx%", Double.toString(x));
        template = template.replace("%ty%", Double.toString(tY));
        template = template.replace("%tagContent%", tagContent);

        outputStream.write(template.getBytes());

        accumulatedY = tY + theight + 5.00;
    }

    public static String[] splitText(String description, int maxWidth) {
        LinkedList<String> lines = new LinkedList<>();
        while (!description.isEmpty()) {
            int width = Math.min(description.length(), maxWidth);
            int terminator = description.substring(0, width).lastIndexOf(" ");
            if (terminator == -1 || width < maxWidth) {
                lines.add(description);
                break;
            }
            lines.add(description.substring(0, terminator));
            description = description.substring(terminator + 1);
        }
        return lines.toArray(new String[]{});
    }

    // # run method
    public static void main(String[] args) throws URISyntaxException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        String description = "http | https | response | egress | web | service";
        String[] descriptionText = splitText(description, 64);
    }
}
