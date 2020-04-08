package sample;

import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application
{
    public static final float WIDTH = 1400;
    public static final float HEIGHT = 1000;
    private double anchorX;
    private double anchorY;
    private double anchorAngleX = 0;
    private double anchorAngelY = 0;
    private final DoubleProperty angleX = new SimpleDoubleProperty(0);
    private final DoubleProperty angleY = new SimpleDoubleProperty(0);

    private final Sphere sphere = new Sphere(150);
    Sphere moon = new Sphere(30);



    @Override
    public void start(Stage primaryStage) throws Exception
    {

        Camera camera = new PerspectiveCamera(true);    // fixedEyeAtCameraZEro: True
        camera.setNearClip(1);
        camera.setFarClip(10000);
        camera.translateZProperty().set(-1000);


        PhongMaterial moonMaterial = new PhongMaterial();
        moonMaterial.setDiffuseMap(new Image(getClass().getResourceAsStream("/earth/moon.jpg")));
        moon.setMaterial(moonMaterial);
        moon.getTransforms().add(new Translate(0, 0, 300));
        moon.setRotationAxis(Rotate.Y_AXIS);




        SmartGroup Earth = new SmartGroup();
        Earth.getChildren().add(prepareEarth());
        //Earth.getChildren().add(moonPath);
        Earth.getChildren().add(moon);










        Slider slider = prepareSlider();
        Earth.translateZProperty().bind(slider.valueProperty());

        Group root = new Group();
        root.getChildren().add(Earth);
        root.getChildren().add(prepareImageView());
        root.getChildren().add(slider);
        //root.getChildren().add(moon);



        Scene scene = new Scene(root, WIDTH, HEIGHT, true);    // depthBuffer: True
        scene.setCamera(camera);
        scene.setFill(Color.SILVER);


        initMouseControl(Earth, scene, primaryStage);




        primaryStage.setTitle("Earth Simulation");
        primaryStage.setScene(scene);
        primaryStage.show();

        prepareAnimation();
    }


    private void prepareAnimation()
    {
        AnimationTimer animationTimer = new AnimationTimer()
        {
            @Override
            public void handle(long l)
            {
                sphere.rotateProperty().set(sphere.getRotate() + 0.2);
                moon.rotateProperty().set(moon.getRotate() + 1);
            }
        };

        animationTimer.start();
    }


    private ImageView prepareImageView()
    {
        Image image = new Image(Main.class.getResourceAsStream("/earth/space.jpg"));
        ImageView imageView = new ImageView(image);

        imageView.setPreserveRatio(true);
        imageView.getTransforms().add(new Translate(-image.getWidth()/2, -image.getHeight()/2, 100));

        return imageView;
    }

    private Slider prepareSlider()
    {
        Slider slider = new Slider();
        slider.setMax(200);
        slider.setMin(-280);
        slider.setPrefWidth(200d);
        slider.setLayoutX(-100);
        slider.setLayoutY(250);
        slider.setTranslateZ(100);
        slider.setShowTickLabels(true);
        slider.setStyle("-fx-base: black");

        return slider;
    }

    private Node prepareEarth()
    {
        PhongMaterial earthMaterial = new PhongMaterial();
        earthMaterial.setDiffuseMap(new Image(getClass().getResourceAsStream("/earth/earthMap.jpg")));
        earthMaterial.setSelfIlluminationMap(new Image(getClass().getResourceAsStream("/earth/earthLights.jpg")));
        earthMaterial.setSpecularMap(new Image(getClass().getResourceAsStream("/earth/earthReflection.jpg")));
        earthMaterial.setBumpMap(new Image(getClass().getResourceAsStream("/earth/earthBumps.jpg")));


        sphere.setRotationAxis(Rotate.Y_AXIS);
        sphere.setMaterial(earthMaterial);

        return sphere;
    }




    private void initMouseControl(SmartGroup group, Scene scene, Stage stage)
    {
        Rotate xRotate;
        Rotate yRotate;

        group.getTransforms().addAll(
                xRotate = new Rotate(0, Rotate.X_AXIS),
                yRotate = new Rotate(0, Rotate.Y_AXIS)
        );

        xRotate.angleProperty().bind(angleX);
        yRotate.angleProperty().bind(angleY);

        // track mouse movement
        scene.setOnMousePressed(mouseEvent ->
        {
            anchorX = mouseEvent.getSceneX();
            anchorY = mouseEvent.getSceneY();

            anchorAngleX = angleX.get();
            anchorAngelY = angleY.get();
        });

        scene.setOnMouseDragged(mouseEvent ->
        {
            angleX.set(anchorAngleX - (anchorY - mouseEvent.getSceneY()));

            angleY.set(anchorAngelY + anchorX - mouseEvent.getSceneX());
        });


        stage.addEventHandler(ScrollEvent.SCROLL, scrollEvent ->
        {
            double delta = scrollEvent.getDeltaY();
            group.translateZProperty().set(group.getTranslateZ() + delta);
        });
    }




    public static void main(String[] args) {
        launch(args);
    }
}




class SmartGroup extends Group
{
    Rotate rotate;
    Transform transform = new Rotate();

    // transform with rotate
    // Transform transform = new Rotate(65, new Point3D(1, 0, 0));
    // box.getTransform().add(transform);


    void rotateByX(int angle)
    {
        rotate = new Rotate(angle, Rotate.X_AXIS);
        transform = transform.createConcatenation(rotate);

        this.getTransforms().clear();
        this.getTransforms().addAll(transform);
    }


    void rotateByY(int angle)
    {
        rotate = new Rotate(angle, Rotate.Y_AXIS);
        transform = transform.createConcatenation(rotate);

        this.getTransforms().clear();
        this.getTransforms().addAll(transform);
    }
}



