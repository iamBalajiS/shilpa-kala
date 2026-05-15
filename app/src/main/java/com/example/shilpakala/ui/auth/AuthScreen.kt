package com.example.shilpakala.ui.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shilpakala.BuildConfig
import com.example.shilpakala.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

@Composable
fun AuthScreen(viewModel: AuthViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val account = runCatching { GoogleSignIn.getSignedInAccountFromIntent(result.data).result }.getOrNull()
        viewModel.signInWithGoogle(account?.email, account?.displayName, account?.idToken)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.AccountCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text("ShilpaKala", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text(if (state.isLogin) stringResource(R.string.login_subtitle) else stringResource(R.string.signup_subtitle))
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(state.email, viewModel::updateEmail, label = { Text(stringResource(R.string.email)) }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            state.password,
            viewModel::updatePassword,
            label = { Text(stringResource(R.string.password)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        if (!state.isLogin) {
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                state.confirmPassword,
                viewModel::updateConfirmPassword,
                label = { Text(stringResource(R.string.confirm_password)) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
        }
        state.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = viewModel::submit, enabled = !state.isLoading, modifier = Modifier.fillMaxWidth().height(54.dp)) {
            if (state.isLoading) CircularProgressIndicator() else Text(if (state.isLogin) stringResource(R.string.login) else stringResource(R.string.sign_up))
        }
        OutlinedButton(onClick = viewModel::toggleMode, modifier = Modifier.fillMaxWidth()) {
            Text(if (state.isLogin) stringResource(R.string.create_new_account) else stringResource(R.string.already_have_account))
        }
        OutlinedButton(
            onClick = {
                val optionsBuilder = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
                if (BuildConfig.GOOGLE_WEB_CLIENT_ID.isNotBlank()) {
                    optionsBuilder.requestIdToken(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                }
                val options = optionsBuilder.build()
                launcher.launch(GoogleSignIn.getClient(context, options).signInIntent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.continue_with_google))
        }
    }
}
