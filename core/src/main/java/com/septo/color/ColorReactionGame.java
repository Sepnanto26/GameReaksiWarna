package com.septo.color;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout; 
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class ColorReactionGame extends ApplicationAdapter {
    // Objek LibGDX
    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout layout; 

    // Logika Warna
    private Color targetColor;
    private float timeToChange; 
    private final float MIN_CHANGE_TIME = 0.5f; 
    private final float MAX_CHANGE_TIME = 2.0f; 
    
    // Konstanta Batas Waktu & Kesulitan
    private final float MAX_REACTION_TIME = 2000f; // Batas waktu reaksi (2000 ms)
    private final float MIN_TIME_CAP = 0.1f; // Batas minimum waktu tunggu (0.1 detik)
    private final float HUE_RANGE_INITIAL = 100f; // Jarak HUE awal (membuat warna berbeda)
    private final float HUE_RANGE_MIN = 20f; // Jarak HUE minimum (membuat warna sangat mirip)
    private final float CHALLENGE_SCORE_THRESHOLD = 500f; // Skor terbaik (ms) untuk mulai meningkatkan kesulitan

    // Logika Tantangan
    private float challengeTime = 0; 
    private boolean isWaitingForClick; 
    private long challengeStartTime;
    private String message = "Klik Layar untuk Mulai!";
    private float bestReactionTime = Float.MAX_VALUE; 

    @Override
    public void create() {
        batch = new SpriteBatch();
        
        // =========================================================
        // PERBAIKAN NULLPOINTER: Inisialisasi targetColor DULU
        // Ini memastikan targetColor tidak null saat dipanggil di resetGame().
        targetColor = new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1f); 
        // =========================================================
        
        // Menggunakan roboto_game.fnt yang sudah dikoreksi
        font = new BitmapFont(Gdx.files.internal("roboto_game.fnt")); 
        
        font.setColor(Color.WHITE);
        layout = new GlyphLayout(); 
        resetGame(); // Sekarang aman dipanggil
    }
    
    /**
     * Menghasilkan warna baru yang semakin mirip dengan warna saat ini seiring skor terbaik membaik.
     * @param currentColor Warna saat ini.
     * @return Warna baru yang menantang.
     */
    private Color getChallengingColor(Color currentColor) {
        // PERBAIKAN NULLPOINTER TAMBAHAN: Jika currentColor secara tidak sengaja null, berikan warna acak murni.
        if (currentColor == null) {
            currentColor = new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1f);
        }
        
        // Hitung faktor kesulitan berdasarkan skor terbaik (0.0 = Terbaik, 1.0 = Buruk)
        float difficultyFactor = MathUtils.clamp(bestReactionTime / CHALLENGE_SCORE_THRESHOLD, 0f, 1f);
        
        // Tentukan rentang HUE
        float hueRange = HUE_RANGE_MIN + (HUE_RANGE_INITIAL - HUE_RANGE_MIN) * difficultyFactor;
        
        // Ambil HUE dari warna saat ini
        float[] hsv = new float[3];
        currentColor.toHsv(hsv);
        float currentHue = hsv[0];

        // Pilih HUE baru dalam rentang hueRange dari currentHue
        float minHue = currentHue - hueRange / 2f;
        float maxHue = currentHue + hueRange / 2f;
        
        // Handle wrap around 360 degrees
        float h = MathUtils.random(minHue, maxHue);
        if (h < 0) h += 360f;
        if (h > 360f) h -= 360f;

        // Saturation dan Lightness
        float s = MathUtils.random(0.8f, 1.0f); 
        float l = MathUtils.random(0.5f, 0.7f); 

        Color c = new Color();
        c.fromHsv(h, s, l);
        return c; 
    }

    private void resetGame() {
        // 1. Logika Warna (Semakin mirip seiring skor membaik)
        targetColor = getChallengingColor(targetColor);

        // 2. Logika Waktu Tunggu (Semakin cepat seiring skor membaik)
        float difficultyFactor = MathUtils.clamp(bestReactionTime / CHALLENGE_SCORE_THRESHOLD, 0f, 1f);

        float maxTime = MIN_CHANGE_TIME + (MAX_CHANGE_TIME - MIN_CHANGE_TIME) * difficultyFactor;
        
        if (maxTime < MIN_TIME_CAP) {
            maxTime = MIN_TIME_CAP;
        }
        
        timeToChange = MathUtils.random(MIN_CHANGE_TIME, maxTime);
        
        // Reset Logika
        isWaitingForClick = false;
        challengeTime = 0;
    }

    @Override
    public void render() {
        // --- 1. LOGIKA GAME ---
        if (message.equals("Tunggu warna berubah...")) {
            timeToChange -= Gdx.graphics.getDeltaTime();

            if (timeToChange <= 0) {
                // TANTANGAN DIMULAI!
                // Di sini, targetColor sudah diatur oleh resetGame()
                isWaitingForClick = true;
                
                challengeStartTime = TimeUtils.nanoTime(); 
                
                message = "!!! KLIK SEKARANG !!!";
            }
        } 
        
        // Periksa Batas Waktu Maksimum (TERLALU LAMBAT)
        if (isWaitingForClick) {
            long currentTime = TimeUtils.nanoTime();
            long elapsedNanoTime = currentTime - challengeStartTime;
            float elapsedMilliSeconds = (float) elapsedNanoTime / 1000000f; 
            
            if (elapsedMilliSeconds > MAX_REACTION_TIME) {
                message = "TERLALU LAMBAT! Klik untuk Coba Lagi.";
                isWaitingForClick = false; 
                resetGame();
                timeToChange = 5f; 
            }
        }
        
        // Input Pemain
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) || Gdx.input.isTouched()) {
            if (isWaitingForClick) {
                // Tantangan berhasil! Hitung waktu reaksi.
                long endTime = TimeUtils.nanoTime();
                long reactionNanoTime = endTime - challengeStartTime; 
                
                challengeTime = (float) reactionNanoTime / 1000000f; 
                
                if (challengeTime < bestReactionTime) {
                    bestReactionTime = challengeTime;
                }

                message = String.format("Waktu Reaksi: %.2f ms", challengeTime);
                isWaitingForClick = false;
            } else if (message.equals("Klik Layar untuk Mulai!") || message.contains("Waktu Reaksi:") || message.contains("TERLALU CEPAT!") || message.contains("TERLALU LAMBAT!")) {
                // Modus reset/mulai game
                message = "Tunggu warna berubah...";
                resetGame(); 
            } else if (message.equals("Tunggu warna berubah...")) {
                // Klik terlalu cepat sebelum tantangan dimulai
                message = "TERLALU CEPAT! Klik untuk Coba Lagi.";
                resetGame();
                timeToChange = 5f; 
            }
        }
        
        // --- 2. TAMPILAN (DRAWING) ---
        
        Gdx.gl.glClearColor(targetColor.r, targetColor.g, targetColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        
        // 1. Pesan Utama (Menggunakan GlyphLayout untuk centering)
        layout.setText(font, message); 
        font.draw(batch, message, 
                 (screenWidth - layout.width) / 2, 
                 screenHeight / 2); 

        // 2. Tampilkan Skor Terbaik (Best Score)
        String scoreMessage = "TERBAIK: --";
        if (bestReactionTime != Float.MAX_VALUE) {
            scoreMessage = String.format("TERBAIK: %.2f ms", bestReactionTime);
        }
        
        // 3. Pesan Skor (Menggunakan GlyphLayout untuk centering)
        layout.setText(font, scoreMessage);
        font.draw(batch, scoreMessage, 
                 (screenWidth - layout.width) / 2, 
                 screenHeight / 2 - layout.height - 20); // Posisikan di bawah pesan utama

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}