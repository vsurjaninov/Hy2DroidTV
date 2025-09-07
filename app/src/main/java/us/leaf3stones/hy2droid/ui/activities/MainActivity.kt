package us.leaf3stones.hy2droid.ui.activities

import android.app.Activity
import android.net.VpnService
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import us.leaf3stones.hy2droid.data.VpnServiceState
import us.leaf3stones.hy2droid.ui.theme.Hy2droidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Hy2droidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier, viewModel: MainActivityViewModel = viewModel()) {
    val focusRequester = remember { FocusRequester() }
    var isStartFocused by remember { mutableStateOf(false) }
    var isStopFocused by remember { mutableStateOf(false) }

    val state by viewModel.state.collectAsState()
    val vpnState by VpnServiceState.state.collectAsState(initial = "disconnected")
    val context = LocalContext.current
    val vpnRequestLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.startVpnService(context)
            }
        }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hysteria 2",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.Start)
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 2.dp),
            thickness = 1.dp
        )

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                modifier = Modifier
                    .padding(horizontal = 2.dp, vertical = 2.dp)
                    .focusRequester(focusRequester)
                    .onFocusChanged { isStartFocused = it.isFocused }
                    .then(
                        if (isStartFocused) {
                            Modifier.border(2.dp, MaterialTheme.colorScheme.inversePrimary, RoundedCornerShape(50.dp))
                        } else {
                            Modifier
                        }
                    ),
                onClick = {
                    val prepIntent = VpnService.prepare(context)
                    if (prepIntent != null) {
                        vpnRequestLauncher.launch(prepIntent)
                    } else {
                        viewModel.startVpnService(context)
                    }
                }
            ) {
                Text(text = "start vpn")
            }

            Button(
                modifier = Modifier
                    .padding(horizontal = 2.dp, vertical = 2.dp)
                    .onFocusChanged { isStopFocused = it.isFocused }
                    .then(
                        if (isStopFocused) {
                            Modifier.border(2.dp, MaterialTheme.colorScheme.inversePrimary, RoundedCornerShape(50.dp))
                        } else {
                            Modifier
                        }
                    ),
                onClick = {
                    viewModel.stopVpnService(context)
                }
            ) {
                Text(text = "stop vpn")
            }
        }

        val config = state.configData
        BasicHysteriaConfigEdit(
            serverAddress = config.server,
            password = config.password,
            sni = config.sni,
            onServerAddressChanged = viewModel::onServerChanged,
            onPasswordChanged = viewModel::onPasswordChanged,
            onSniChanged = viewModel::onSniChanged,
            onConfigConfirmed = viewModel::onConfigConfirmed,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = vpnState,
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
            style = MaterialTheme.typography.titleMedium
        )

        if (state.shouldShowConfigInvalidReminder) {
            AlertDialog(
                onDismissRequest = { viewModel.onConfigInvalidReminderDismissed() },
                confirmButton = {
                    Text(
                        text = "ok",
                        modifier = Modifier.clickable { viewModel.onConfigInvalidReminderDismissed() },
                        fontSize = 16.sp
                    )
                }, title = {
                    Text(
                        text = "Invalid Config",
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 20.sp
                    )
                }, text = {
                    Text(text = "Configuration data is incomplete. Only the \"sni\" field is optional.")
                })
        }
    }

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }
}

@Composable
fun BasicHysteriaConfigEdit(
    serverAddress: String,
    password: String,
    sni: String,
    onServerAddressChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onSniChanged: (String) -> Unit,
    onConfigConfirmed: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSaveFocused by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = serverAddress,
            placeholder = {
                Text(text = "server address")
            },
            onValueChange = onServerAddressChanged,
            modifier = Modifier.fillMaxWidth(), maxLines = 1
        )
        OutlinedTextField(
            value = password,
            placeholder = {
                Text(text = "password")
            },
            onValueChange = onPasswordChanged,
            modifier = Modifier.fillMaxWidth(), maxLines = 1
        )
        OutlinedTextField(
            value = sni,
            placeholder = {
                Text(text = "sni")
            },
            onValueChange = onSniChanged,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1
        )
        Button(
            onClick = onConfigConfirmed,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .onFocusChanged { isSaveFocused = it.isFocused }
                .then(
                    if (isSaveFocused) {
                        Modifier.border(2.dp, MaterialTheme.colorScheme.inversePrimary, RoundedCornerShape(50.dp))
                    } else {
                        Modifier
                    }
                ),
        ) {
            Text(text = "save")
        }
    }
}