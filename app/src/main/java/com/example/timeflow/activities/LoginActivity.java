package com.example.timeflow.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.timeflow.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        // Animation d'entrée
        findViewById(R.id.cardLogin).startAnimation(
            android.view.animation.AnimationUtils.loadAnimation(this, R.anim.slide_up)
        );

        // Firebase
        auth = FirebaseAuth.getInstance();

        // Vérifier si l'utilisateur est déjà connecté
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
            return;
        }

        // Vues
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> login());

        // Lien vers Register
        findViewById(R.id.tvGoRegister).setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
    }

    private void login() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Vérification email
        if (email.isEmpty()) {
            etEmail.setError("Email requis");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email invalide");
            etEmail.requestFocus();
            return;
        }

        // Vérification mot de passe
        if (password.isEmpty()) {
            etPassword.setError("Mot de passe requis");
            etPassword.requestFocus();
            return;
        }

        // 🔥 CONNEXION FIREBASE (LA VRAIE)
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        Toast.makeText(
                                LoginActivity.this,
                                "Connexion réussie",
                                Toast.LENGTH_SHORT
                        ).show();

                        // Aller vers Home
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();

                    } else {
                        String errorMessage = "Email ou mot de passe incorrect";
                        if (task.getException() != null && task.getException().getMessage() != null) {
                            String exceptionMessage = task.getException().getMessage();
                            if (exceptionMessage.contains("user-not-found")) {
                                errorMessage = "Aucun compte trouvé avec cet email";
                            } else if (exceptionMessage.contains("wrong-password")) {
                                errorMessage = "Mot de passe incorrect";
                            } else if (exceptionMessage.contains("invalid-email")) {
                                errorMessage = "Format d'email invalide";
                            }
                        }
                        Toast.makeText(
                                LoginActivity.this,
                                errorMessage,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}
