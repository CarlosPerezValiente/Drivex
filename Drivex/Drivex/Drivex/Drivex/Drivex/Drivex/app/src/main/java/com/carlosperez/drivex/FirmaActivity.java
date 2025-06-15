package com.carlosperez.drivex;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;

public class FirmaActivity extends AppCompatActivity {

    private SignatureView signatureView;
    private int idHorario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Firma");
        signatureView = new SignatureView(this);
        setContentView(signatureView);

        idHorario = getIntent().getIntExtra("idHorario", -1);

        Button btnGuardar = new Button(this);
        btnGuardar.setText("Guardar Firma");
        btnGuardar.setY(1500);  // Ajusta la posición según el diseño
        btnGuardar.setX(100);

        addContentView(btnGuardar, new android.widget.FrameLayout.LayoutParams(
                android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
        ));

        btnGuardar.setOnClickListener(v -> guardarFirma());
    }

    private void guardarFirma() {
        try {
            File file = new File(getFilesDir(), "firma_" + idHorario + ".png");
            FileOutputStream fos = new FileOutputStream(file);
            signatureView.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            Toast.makeText(this, "Firma guardada correctamente", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar la firma", Toast.LENGTH_SHORT).show();
        }
    }

    // Vista personalizada para la firma
    private static class SignatureView extends View {

        private Path path = new Path();
        private Paint paint = new Paint();
        private Bitmap bitmap;
        private Canvas canvas;

        public SignatureView(FirmaActivity context) {
            super(context);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(8);
            paint.setStyle(Paint.Style.STROKE);
        }

        @Override
        protected void onDraw(Canvas c) {
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
                canvas = new Canvas(bitmap);
                canvas.drawColor(Color.WHITE);
            }
            canvas.drawPath(path, paint);
            c.drawBitmap(bitmap, 0, 0, null);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    path.lineTo(x, y);
                    break;
            }
            invalidate();
            return true;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }
    }
}
