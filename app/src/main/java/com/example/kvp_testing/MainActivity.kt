package com.example.kvp_testing

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.launch

val Context.dataStore by preferencesDataStore(name = "settings")

val USERNAME_KEY = stringPreferencesKey("username")
val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications_enabled")

suspend fun saveSettings(
    context: Context,
    username: String,
    notificationsEnabled: Boolean
) {
    context.dataStore.edit { preferences ->
        preferences[USERNAME_KEY] = username
        preferences[NOTIFICATIONS_KEY] = notificationsEnabled
    }
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DataStoreScreen()
        }
    }
}

@Composable
fun DataStoreScreen() {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Read saved values from DataStore
    val preferences by context.dataStore.data.collectAsState(
        initial = null
    )

    // Put the saved values into the UI
    var username by remember(preferences) {
        mutableStateOf(
            preferences?.get(USERNAME_KEY) ?: ""
        )
    }

    var notificationsEnabled by remember(preferences) {
        mutableStateOf(
            preferences?.get(NOTIFICATIONS_KEY) ?: false
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text("My Settings")

        TextField(
            value = username,
            onValueChange = {
                username = it
            },
            label = {
                Text("Username")
            }
        )

        Row {
            Checkbox(
                checked = notificationsEnabled,
                onCheckedChange = {
                    notificationsEnabled = it
                }
            )

            Text("Enable notifications")
        }

        Button(
            onClick = {

                scope.launch {

                    context.dataStore.edit { preferences ->

                        preferences[USERNAME_KEY] = username

                        preferences[NOTIFICATIONS_KEY] =
                            notificationsEnabled
                    }
                }
            }
        ) {
            Text("Save Settings")
        }
    }
}