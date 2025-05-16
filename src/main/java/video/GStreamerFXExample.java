package video;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.freedesktop.gstreamer.*;
import org.freedesktop.gstreamer.elements.AppSink;

import java.nio.ByteBuffer;

public class GStreamerFXExample extends Application {
    private Canvas canvas;
    private PixelWriter pixelWriter;

    public static void main(String[] args) {
        Gst.init("GStreamerFXExample", args);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        canvas = new Canvas(640, 480);
        pixelWriter = canvas.getGraphicsContext2D().getPixelWriter();

        StackPane root = new StackPane(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("GStreamer + JavaFX");
        primaryStage.show();

        setupPipeline();
    }

    private void setupPipeline() {
        String pipelineStr = "videotestsrc ! video/x-raw,format=RGB,width=640,height=480 ! appsink name=sink";
        Pipeline pipeline = (Pipeline) Gst.parseLaunch(pipelineStr);
        AppSink sink = (AppSink) pipeline.getElementByName("sink");

        sink.set("emit-signals", true);
        sink.connect((AppSink.NEW_SAMPLE) sink1 -> {
            Sample sample = sink1.pullSample();
            Buffer buffer = sample.getBuffer();
            Caps caps = sample.getCaps();
            Structure struct = caps.getStructure(0);

            int width = struct.getInteger("width");
            int height = struct.getInteger("height");

            ByteBuffer bb = buffer.map(false);
            if (bb != null) {
                GraphicsContext gc = canvas.getGraphicsContext2D();
                WritablePixelFormat<ByteBuffer> format = WritablePixelFormat.getByteBgraInstance();
                pixelWriter.setPixels(0, 0, width, height, format, bb, width * 3);
                buffer.unmap();
            }

            sample.dispose();
            return FlowReturn.OK;
        });

        pipeline.play();
    }
}
