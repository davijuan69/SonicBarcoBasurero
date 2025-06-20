package src.screens.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import src.main.Main;
import src.utils.ThreadSecureWorld;
import src.screens.components.LayersManager;
import src.screens.components.PowerView;
import src.screens.components.chat.Chat;
import src.screens.game.gameLayers.GameLayerManager;
import src.screens.uiScreens.UIScreen;
import src.utils.ScorePlayer;
import src.utils.SecondsTimer;
import src.utils.indicators.BorderIndicator;
import src.utils.indicators.IndicatorManager;
import src.utils.managers.CameraShakeManager;
import src.utils.managers.SpawnManager;
import src.utils.sound.SingleSoundManager;
import src.world.ActorBox2d;
import src.world.entities.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static src.utils.constants.Constants.TIME_MINUTES_GAME;

public class GameScreen extends UIScreen {
    private final Stage stage;
    private final World world;
    public ThreadSecureWorld threadSecureWorld;
    private Boolean isLoad;

    private final ArrayList<ActorBox2d> actors;
    private final Vector2 lastPosition;
    private Float sendTime;
    private final SecondsTimer timeGame;
    private final HashMap<Integer, ScorePlayer> scorePlayers;

    private final Random random;
    public Vector2 lobbyPlayer;
    public SpawnManager spawnMirror;
    public ArrayList<Vector2> spawnPlayer;

    private final CameraShakeManager cameraShakeManager;
    private IndicatorManager mirrorIndicators;
    private BorderIndicator maxScoreIndicator;
    private Integer idTargetMaxScore;

    private LayersManager layersManager;
    private Label odsPointsLabel;
    private Label gameTimeLabel;
    private Chat chat;
    private PowerView imagePower;

    private GameLayerManager gameLayerManager;

    private Sound mirrorChangeSound;
    private final Box2DDebugRenderer debugRenderer;

    private Player player;

    public GameScreen(Main main){
        super(main);

        actors = new ArrayList<>();
        stage = new Stage(new ScreenViewport());
        world = new World(new Vector2(0, -30f), true);
        threadSecureWorld = new ThreadSecureWorld(world);

        world.setContactListener(new GameContactListener(this));
        lastPosition = new Vector2();
        sendTime = 0f;
        scorePlayers = new HashMap<>();
        timeGame = new SecondsTimer(TIME_MINUTES_GAME, 0);

        random = new Random();
        spawnMirror = new SpawnManager();
        spawnPlayer = new ArrayList<>();

        idTargetMaxScore = -1;

        initSounds();

        debugRenderer = new Box2DDebugRenderer();

        cameraShakeManager = new CameraShakeManager((OrthographicCamera) stage.getCamera());
        isLoad = false;
    }

    private void initSounds() {
    }

    public HashMap<Integer, ScorePlayer> getScorePlayers() {
        return scorePlayers;
    }

    public World getWorld() {
        return world;
    }

    public Chat getChat() {
        return chat;
    }

    public void clearAll() {
        for (ActorBox2d actor : actors) actor.detach();

        stage.clear();
        stageUI.clear();
        actors.clear();
        spawnMirror.clear();
    }

    public void endGame() {
        threadSecureWorld.clearModifications();
        threadSecureWorld.addModification(() -> {
            clearAll();
            timeGame.resetTimer();
            main.changeScreen(Main.Screens.ENDGAME);
            isLoad = false;
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stageUI);
        SingleSoundManager.getInstance().setSoundTracks(Main.SoundTrackType.GAME);

        player = new Player(world, 1800f, 600f, main.getAssetManager());
        stage.addActor(player);
    }

    private void initUI() {
        layersManager = new LayersManager(stageUI, 6);

        Image timeImage = new Image(main.getAssetManager().get("ui/icons/clock.png", Texture.class));
        gameTimeLabel = new Label(timeGame.toString(), new Label.LabelStyle(main.fonts.briBorderFont, null));
        gameTimeLabel.setAlignment(Align.left);
        gameTimeLabel.setFontScale(1);

        Image coinImage = new Image(main.getAssetManager().get("ui/icons/coinIcon.png", Texture.class));
        coinImage.setScaling(Scaling.fit);
        odsPointsLabel = new Label("0", new Label.LabelStyle(main.fonts.briBorderFont, null));
        odsPointsLabel.setAlignment(Align.left);
        odsPointsLabel.setFontScale(0.8f);

        chat = new Chat(new Label.LabelStyle(main.fonts.interFont, null));

        //mirrorIndicators = new IndicatorManager(main.getAssetManager().get("ui/indicators/mirrorIndicator.png", Texture.class));
        //maxScoreIndicator = new BorderIndicator(main.getAssetManager().get("ui/indicators/maxScoreIndicator.png", Texture.class), new Vector2(0, 0));
        maxScoreIndicator.setVisible(false);

        imagePower = new PowerView(main.getAssetManager());

        stage.addActor(mirrorIndicators);
        stage.addActor(maxScoreIndicator);

        layersManager.setZindex(0);
        layersManager.getLayer().top().pad(10);
        layersManager.getLayer().add(timeImage).padRight(5).size(64);
        layersManager.getLayer().add(gameTimeLabel);
        layersManager.getLayer().add().expandX();
        layersManager.getLayer().row().padTop(5);
        layersManager.getLayer().add(coinImage).padRight(5).size(48);
        layersManager.getLayer().add(odsPointsLabel).left();

        layersManager.setZindex(1);
        layersManager.getLayer().add(chat).grow();

        layersManager.setZindex(2);
        layersManager.getLayer().bottom();
        layersManager.getLayer().add().expandX();
        layersManager.getLayer().add(imagePower).width(182).height(50).row();

        gameLayerManager = new GameLayerManager(this, stageUI);
    }

    public void actLogic(float delta) {
        if (timeGame.isFinished()) endGame();

        timeGame.update(delta);
        stage.act();
        threadSecureWorld.step(delta, 6, 2);
    }

    private void actUI() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.4f, 0.5f, 0.8f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (!isLoad) return;

        OrthographicCamera camera = (OrthographicCamera) stage.getCamera();
        OrthographicCamera cameraUI = (OrthographicCamera) stageUI.getCamera();

        cameraUI.position.x = camera.position.x;
        cameraUI.position.y = camera.position.y;

        cameraShakeManager.update(delta);
        camera.update();
        cameraUI.update();

        layersManager.setCenterPosition(camera.position.x, camera.position.y);
        gameLayerManager.setCenterPosition(cameraUI.position.x, cameraUI.position.y);
        actUI();

        if (player != null) {
            player.update(delta);
            player.draw(stage.getBatch());
        }

        stage.draw();
        stageUI.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gameLayerManager.setVisibleWithSound(!gameLayerManager.isVisible());
        }

        actLogic(delta);
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.postRunnable(() -> {
            float cameraZoom = 1280.0f / width;
            if (cameraZoom > 1.3f) cameraZoom = 1.3f;
            OrthographicCamera camera = (OrthographicCamera) stage.getCamera();
            camera.zoom = cameraZoom;

            stage.getViewport().update(width, height, false);
            stageUI.getViewport().update(width, height, false);
        });
    }

    public void addCameraShake(Float time, Float force) {
        cameraShakeManager.addShake(time, force);
    }

    public void addCameraShakeProximity(Vector2 position, float maxDistance, float time, float maxForce) {
    }

    @Override
    public void dispose() {
        clearAll();
        world.dispose();
    }

    private static class GameContactListener implements ContactListener {
        private final GameScreen game;

        public GameContactListener(GameScreen gameScreen) {
            this.game = gameScreen;
        }

        @Override
        public void beginContact(Contact contact) {
            ActorBox2d actorA = (ActorBox2d) contact.getFixtureA().getUserData();
            ActorBox2d actorB = (ActorBox2d) contact.getFixtureB().getUserData();

            if (actorA == null || actorB == null) return;

            actorA.beginContactWith(actorB, game);
            actorB.beginContactWith(actorA, game);
        }

        @Override
        public void endContact(Contact contact) {
        }

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {
        }

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {
        }
    }
}
