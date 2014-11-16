package de.eidottermihi.rpicheck.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.CursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.common.base.Strings;

import de.eidottermihi.rpicheck.R;
import de.eidottermihi.rpicheck.db.CommandBean;
import de.eidottermihi.rpicheck.db.DeviceDbHelper;
import de.eidottermihi.rpicheck.db.RaspberryDeviceBean;
import de.eidottermihi.rpicheck.fragment.CommandPlaceholdersDialog;
import de.eidottermihi.rpicheck.fragment.CommandPlaceholdersDialog.PlaceholdersDialogListener;
import de.eidottermihi.rpicheck.fragment.PassphraseDialog;
import de.eidottermihi.rpicheck.fragment.PassphraseDialog.PassphraseDialogListener;
import de.eidottermihi.rpicheck.fragment.RunCommandDialog;

public class CustomCommandActivity extends SherlockFragmentActivity implements
		OnItemClickListener, PassphraseDialogListener,
		PlaceholdersDialogListener {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(CustomCommandActivity.class);

	private RaspberryDeviceBean currentDevice;

	private ListView commandListView;

	private DeviceDbHelper deviceDb = new DeviceDbHelper(this);

	private Cursor fullCommandCursor;

	private Pattern placeHolderPattern = Pattern
			.compile("(\\$\\{[a-zA-z0-9]+\\})");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_commands);
		this.commandListView = (ListView) findViewById(R.id.commandListView);

		// Show the Up button in the action bar.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		Bundle extras = this.getIntent().getExtras();
		if (extras != null && extras.get("pi") != null) {
			LOGGER.debug("onCreate: get currentDevice out of intent.");
			currentDevice = (RaspberryDeviceBean) extras.get("pi");
		} else if (savedInstanceState.getSerializable("pi") != null) {
			LOGGER.debug("onCreate: get currentDevice out of savedInstanceState.");
			currentDevice = (RaspberryDeviceBean) savedInstanceState
					.getSerializable("pi");
		}
		if (currentDevice != null) {
			LOGGER.debug("Setting activity title for device.");
			getSupportActionBar().setTitle(currentDevice.getName());
			LOGGER.debug("Initializing ListView");
			this.initListView(currentDevice);
		} else {
			LOGGER.debug("No current device! Setting no title");
		}

	}

	/**
	 * Init ListView with commands.
	 * 
	 * @param pi
	 */
	private void initListView(RaspberryDeviceBean pi) {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				fullCommandCursor = deviceDb.getFullCommandCursor();
				return null;
			}

			@Override
			protected void onPostExecute(Void r) {
				CommandAdapter commandsAdapter = new CommandAdapter(
						CustomCommandActivity.this, fullCommandCursor,
						CursorAdapter.FLAG_AUTO_REQUERY);
				commandListView.setAdapter(commandsAdapter);
				commandListView
						.setOnItemClickListener(CustomCommandActivity.this);
				// commandListView.setOnItemLongClickListener(CustomCommandActivity.this);
				registerForContextMenu(commandListView);
			}
		}.execute();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_commands, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onCreateContextMenu(final ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.commandListView) {
			final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			LOGGER.debug("Creating context menu for command id = {}.", info.id);
			CommandBean cmd = deviceDb.readCommand(info.id);
			menu.setHeaderTitle(cmd.getName());
			menu.add(Menu.NONE, 1, 1, R.string.command_context_edit);
			menu.add(Menu.NONE, 2, 2, R.string.command_context_delete);
			menu.add(Menu.NONE, 3, 3, R.string.command_context_run);

		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			break;
		case R.id.menu_new_command:
			// init intent
			Intent newCommandIntent = new Intent(CustomCommandActivity.this,
					NewCommandActivity.class);
			newCommandIntent.putExtras(this.getIntent().getExtras());
			this.startActivityForResult(newCommandIntent,
					NewCommandActivity.REQUEST_NEW);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		LOGGER.debug("Context item selected for command id {}.", info.id);
		int menuItemIndex = item.getItemId();
		switch (menuItemIndex) {
		case 1:
			Intent newCommandIntent = new Intent(CustomCommandActivity.this,
					NewCommandActivity.class);
			newCommandIntent.putExtras(this.getIntent().getExtras());
			newCommandIntent.putExtra(NewCommandActivity.CMD_KEY_EDIT, info.id);
			this.startActivityForResult(newCommandIntent,
					NewCommandActivity.REQUEST_EDIT);
			break;
		case 2:
			this.deleteCommand(info.id);
			break;
		case 3:
			this.runCommand(info.id);
			break;
		default:
			break;
		}
		return true;
	}

	private void deleteCommand(final long id) {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				deviceDb.deleteCommand(id);
				return null;
			}

			@Override
			protected void onPostExecute(Void r) {
				CustomCommandActivity.this.initListView(currentDevice);
			}
		}.execute();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == NewCommandActivity.REQUEST_NEW
				&& resultCode == RESULT_OK) {
			// new cmd saved, update...
			Toast.makeText(this, R.string.toast_command_saved,
					Toast.LENGTH_SHORT).show();
			initListView(currentDevice);
		} else if (requestCode == NewCommandActivity.REQUEST_EDIT
				&& resultCode == RESULT_OK) {
			Toast.makeText(this, R.string.toast_command_updated,
					Toast.LENGTH_SHORT).show();
			initListView(currentDevice);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (currentDevice != null) {
			LOGGER.debug("Writing currentDevice in outState.");
			outState.putSerializable("pi", currentDevice);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int itemPos,
			long itemId) {
		LOGGER.debug("Command pos {} clicked. Item _id = {}.", itemPos, itemId);
		runCommand(itemId);
	}

	private void runCommand(long commandId) {
		ConnectivityManager connMgr = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			if (currentDevice.getAuthMethod().equals(
					NewRaspiAuthActivity.SPINNER_AUTH_METHODS[2])
					&& Strings.isNullOrEmpty(currentDevice.getKeyfilePass())) {
				// must ask for key passphrase first
				LOGGER.debug("Asking for key passphrase.");
				// dirty hack, saving commandId as "dialog type"
				final String dialogType = commandId + "";
				final DialogFragment passphraseDialog = new PassphraseDialog();
				final Bundle args = new Bundle();
				args.putString(PassphraseDialog.KEY_TYPE, dialogType);
				passphraseDialog.setArguments(args);
				passphraseDialog.setCancelable(false);
				passphraseDialog
						.show(getSupportFragmentManager(), "passphrase");
			} else {
				LOGGER.debug("Opening command dialog.");
				openCommandDialog(commandId, currentDevice.getKeyfilePass());
			}
		} else {
			Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (deviceDb != null) {
			deviceDb.close();
		}
	}

	/**
	 * Opens the command dialog.
	 * 
	 * @param keyPassphrase
	 *            nullable: key passphrase
	 */
	private void openCommandDialog(final long commandId,
			final String keyPassphrase) {
		final DialogFragment runCommandDialog = new RunCommandDialog();
		final Bundle args = new Bundle();
		final CommandBean command = deviceDb.readCommand(commandId);
		final ArrayList<String> placeholders = parsePlaceholders(command
				.getCommand());
		if (!placeholders.isEmpty()) {
			// need to get replacements for placeholders first
			DialogFragment placeholderDialog = new CommandPlaceholdersDialog();
			Bundle args2 = new Bundle();
			args2.putStringArrayList(
					CommandPlaceholdersDialog.ARG_PLACEHOLDERS, placeholders);
			args2.putSerializable(CommandPlaceholdersDialog.ARG_COMMAND,
					command);
			args2.putString(CommandPlaceholdersDialog.ARG_PASSPHRASE,
					keyPassphrase);
			placeholderDialog.setArguments(args2);
			placeholderDialog.show(getSupportFragmentManager(), "placeholders");
			return;
		}
		args.putSerializable("pi", currentDevice);
		args.putSerializable("cmd", command);
		if (keyPassphrase != null) {
			args.putString("passphrase", keyPassphrase);
		}
		runCommandDialog.setArguments(args);
		runCommandDialog.show(getSupportFragmentManager(), "runCommand");
	}

	private ArrayList<String> parsePlaceholders(String commandString) {
		ArrayList<String> placeholders = new ArrayList<String>();
		Matcher m = placeHolderPattern.matcher(commandString);
		while (m.find()) {
			String placeholder = m.group();
			placeholders.add(placeholder);
		}
		return placeholders;
	}

	@Override
	public void onPassphraseOKClick(DialogFragment dialog, String passphrase,
			boolean savePassphrase, String type) {
		LOGGER.debug("Key passphrase entered.");
		if (savePassphrase) {
			LOGGER.debug("Saving passphrase..");
			currentDevice.setKeyfilePass(passphrase);
			currentDevice.setModifiedAt(new Date());
			new Thread() {
				@Override
				public void run() {
					deviceDb.update(currentDevice);
				}
			}.start();
		}
		// dirty hack: type is commandId
		Long commandId = Long.parseLong(type);
		LOGGER.debug("Starting command dialog for command id " + commandId);
		openCommandDialog(commandId, passphrase);

	}

	@Override
	public void onPassphraseCancelClick() {
		// do nothing
	}

	@Override
	public void onPlaceholdersOKClick(DialogFragment dialog,
			CommandBean command, String keyPassphrase) {
		DialogFragment runCommandDialog = new RunCommandDialog();
		Bundle args = new Bundle();
		args.putSerializable("pi", currentDevice);
		args.putSerializable("cmd", command);
		if (keyPassphrase != null) {
			args.putString("passphrase", keyPassphrase);
		}
		runCommandDialog.setArguments(args);
		runCommandDialog.show(getSupportFragmentManager(), "runCommand");

	}

	@Override
	public void onPlaceholdersCancelClick() {
		// do nothing
	}

}
