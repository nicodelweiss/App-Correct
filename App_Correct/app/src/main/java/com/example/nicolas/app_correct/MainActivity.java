package com.example.nicolas.app_correct;

import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.apache.commons.io.FileUtils;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.ISVNReporterBaton;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.List;

import static com.example.nicolas.app_correct.Crypto.calculateRFC2104HMAC;
import static com.example.nicolas.app_correct.Load.setupLibrary;
import static com.example.nicolas.app_correct.Save.addDir;


public class MainActivity extends AppCompatActivity {

    Button buttonConnect;
    Button buttonLoad;
    Button buttonSave;
    Button buttonDisconnect;

    WifiManager wifi;
    WifiScanReceiver wifiReceiver;
    String info;
    String ssid;
    String ssid_tmp;
    String hmac;
    String[] parts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WifiScanReceiver();

        // Links to button clicks
        buttonConnect = (Button) findViewById(R.id.buttonConnect);

        // Capture button clicks
        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Application", "Connect");
                wifi.setWifiEnabled(true); // activate wifi
                System.out.println("before start scan");
                wifi.startScan();
                //WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
                //url_repo = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                //System.out.println(url_repo );


            }
        });

        /*String[] PERMS_INITIAL = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        ActivityCompat.requestPermissions(this, PERMS_INITIAL, 127);*/

        // Links to button clicks
        buttonLoad = (Button) findViewById(R.id.buttonLoad);

        // Capture button clicks
        buttonLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                Log.i("Application", "Load");

                setupLibrary();

                //SVNURL url = SVNURL.parseURIEncoded("http://192.168.1.1/svn/TraceOp/");

                SVNURL url = null;
                try {
                    url = SVNURL.parseURIEncoded("https://192.168.1.1/svn/TraceOp/");
                    /*
         * Credentials to use for authentication.
         */
                    //String userName = "jinigmichou";
                    //String s_userPassword = "pass1988a";
                    //char[] userPassword = s_userPassword.toCharArray();
                    String userName = "http";
                    char[] userPassword = {};


        /*
         * Prepare filesystem directory (export destination).
         */
                    // Get the directory for the user's public pictures directory.


                    File sdCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                    //System.out.println(sdCard);

                    File exportDirCheckout = new File (sdCard.getAbsolutePath(), "/svnContent/");

                    if (exportDirCheckout.exists()) {
                        try {
                            FileUtils.deleteDirectory(exportDirCheckout);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    //exportDirCheckout.mkdirs();


            /*SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.IO_ERROR, "Path ''{0}'' already exists", exportDir);
            throw new SVNException(err);}*/

        /*
         * Create an instance of SVNRepository class. This class is the main entry point
         * for all "low-level" Subversion operations supported by Subversion protocol.
         *
         * These operations includes browsing, update and commit operations. See
         * SVNRepository methods javadoc for more details.
         */
                    SVNRepository repository = SVNRepositoryFactory.create(url);

        /*
         * User's authentication information (name/password) is provided via  an
         * ISVNAuthenticationManager  instance.  SVNWCUtil  creates  a   default
         * authentication manager given user's name and password.
         *
         * Default authentication manager first attempts to use provided user name
         * and password and then falls back to the credentials stored in the
         * default Subversion credentials storage that is located in Subversion
         * configuration area. If you'd like to use provided user name and password
         * only you may use BasicAuthenticationManager class instead of default
         * authentication manager:
         *
         *  authManager = new BasicAuthenticationsManager(userName, userPassword);
         *
         * You may also skip this point - anonymous access will be used.
         */
                    ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(userName, userPassword);
                    repository.setAuthenticationManager(authManager);

        /*
         * Get type of the node located at URL we used to create SVNRepository.
         *
         * "" (empty string) is path relative to that URL,
         * -1 is value that may be used to specify HEAD (latest) revision.
         */
                    SVNNodeKind nodeKind = repository.checkPath("", -1);
                    if (nodeKind == SVNNodeKind.NONE) {
                        SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.UNKNOWN, "No entry at URL ''{0}''", url);
                        throw new SVNException(err);
                    } else if (nodeKind == SVNNodeKind.FILE) {
                        SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.UNKNOWN, "Entry at URL ''{0}'' is a file while directory was expected", url);
                        throw new SVNException(err);
                    }

        /*
         * Get latest repository revision. We will export repository contents at this very revision.
         */
                    long latestRevision = repository.getLatestRevision();

        /*
         * Create reporterBaton. This class is responsible for reporting 'wc state' to the server.
         *
         * In this example it will always report that working copy is empty to receive update
         * instructions that are sufficient to create complete directories hierarchy and get full
         * files contents.
         */
                    ISVNReporterBaton reporterBaton = new Load.ExportReporterBaton(latestRevision);

        /*
         * Create editor. This class will process update instructions received from the server and
         * will create directories and files accordingly.
         *
         * As we've reported 'emtpy working copy', server will only send 'addDir/addFile' instructions
         * and will never ask our editor implementation to modify a file or directory properties.
         */
                    ISVNEditor exportEditor = new Load.ExportEditor(exportDirCheckout);

        /*
         * Now ask SVNKit to perform generic 'update' operation using our reporter and editor.
         *
         * We are passing:
         *
         * - revision from which we would like to export
         * - null as "target" name, to perform export from the URL SVNRepository was created for,
         *   not from some child directory.
         * - reporterBaton
         * - exportEditor.
         */

                    repository.update(latestRevision, null, true, reporterBaton, exportEditor);

                    System.out.println("Exported revision: " + latestRevision);


                    SVNClientManager ourClientManager = SVNClientManager.newInstance(null,
                            repository.getAuthenticationManager());
                    SVNUpdateClient updateClient = ourClientManager.getUpdateClient();
                    updateClient.setIgnoreExternals(false);

                    System.out.println("before doing checkout");

                    //        updateClient.doCheckout(url, exportDirCheckout, SVNRevision.HEAD, SVNRevision.HEAD,true);
                    updateClient.doCheckout(url, exportDirCheckout, SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.INFINITY , true);

                    System.out.println("Checkout Done");

                } catch (SVNException e) {
                    e.printStackTrace();
                }
            }
        });

        // Links to button clicks
        buttonSave = (Button) findViewById(R.id.buttonSave);

        // Capture button clicks
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Log.i("Application", "Save");

         /*
         * Initialize the library. It must be done before calling any
         * method of the library.
         */
                setupLibrary();

        /*
         * Run commit example and process error if any.
         */
              /*
         * URL that points to repository.
         */
                SVNURL url = null;
                try {
                    url = SVNURL.parseURIEncoded("https://192.168.1.1/svn/TraceOp/");
                } catch (SVNException e) {
                    e.printStackTrace();
                }
                //  SVNURL url = null;
                //  try {
                //      url = SVNURL.parseURIEncoded("https://github.com/jinigmichou/Test/trunk/");
                //  } catch (SVNException e) {
                //      e.printStackTrace();
                //  }
        /*
         * Credentials to use for authentication.
         */
                //String userName = "jinigmichou";
                //String s_userPassword = "pass1988a";
                //char[] userPassword = s_userPassword.toCharArray();
                String userName = "http";
                char[] userPassword = {};


                String tmp_stirng = "To fill";
                //
                byte[] contents = tmp_stirng.getBytes();
                //byte[] contents = arg[1].getBytes();


        /*
         * Create an instance of SVNRepository class. This class is the main entry point
         * for all "low-level" Subversion operations supported by Subversion protocol.
         *
         * These operations includes browsing, update and commit operations. See
         * SVNRepository methods javadoc for more details.
         */
                SVNRepository repository = null;
                try {
                    repository = SVNRepositoryFactory.create(url);
                } catch (SVNException e) {
                    e.printStackTrace();
                }

        /*
         * User's authentication information (name/password) is provided via  an
         * ISVNAuthenticationManager  instance.  SVNWCUtil  creates  a   default
         * authentication manager given user's name and password.
         *
         * Default authentication manager first attempts to use provided user name
         * and password and then falls back to the credentials stored in the
         * default Subversion credentials storage that is located in Subversion
         * configuration area. If you'd like to use provided user name and password
         * only you may use BasicAuthenticationManager class instead of default
         * authentication manager:
         *
         *  authManager = new BasicAuthenticationsManager(userName, userPassword);
         *
         * You may also skip this point - anonymous access will be used.
         */
                ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(userName, userPassword);
                repository.setAuthenticationManager(authManager);

        /*
         * Get type of the node located at URL we used to create SVNRepository.
         *
         * "" (empty string) is path relative to that URL,
         * -1 is value that may be used to specify HEAD (latest) revision.
         */
                SVNNodeKind nodeKind = null;
                try {
                    nodeKind = repository.checkPath("", -1);
                } catch (SVNException e) {
                    e.printStackTrace();
                }

        /*
         * Checks  up  if the current path really corresponds to a directory. If
         * it doesn't, the program exits. SVNNodeKind is that one who says  what
         * is located at a path in a revision.
         */
                if (nodeKind == SVNNodeKind.NONE) {
                    SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.UNKNOWN, "No entry at URL ''{0}''", url);
                    try {
                        throw new SVNException(err);
                    } catch (SVNException e) {
                        e.printStackTrace();
                    }
                } else if (nodeKind == SVNNodeKind.FILE) {
                    SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.UNKNOWN, "Entry at URL ''{0}'' is a file while directory was expected", url);
                    try {
                        throw new SVNException(err);
                    } catch (SVNException e) {
                        e.printStackTrace();
                    }
                }

        /*
         * Get exact value of the latest (HEAD) revision.
         */
                long latestRevision = 0;
                try {
                    latestRevision = repository.getLatestRevision();
                } catch (SVNException e) {
                    e.printStackTrace();
                }
                System.out.println("Repository latest revision (before committing): " + latestRevision);

        /*
         * Gets an editor for committing the changes to  the  repository.  NOTE:
         * you  must not invoke methods of the SVNRepository until you close the
         * editor with the ISVNEditor.closeEdit() method.
         *
         * commitMessage will be applied as a log message of the commit.
         *
         * ISVNWorkspaceMediator instance will be used to store temporary files,
         * when 'null' is passed, then default system temporary directory will be used to
         * create temporary files.
         */
                ISVNEditor editor = null;
                try {
                    editor = repository.getCommitEditor("directory and file added", null);
                } catch (SVNException e) {
                    e.printStackTrace();
                }

        /*
         * Add a directory and a file within that directory.
         *
         * SVNCommitInfo object contains basic information on the committed revision, i.e.
         * revision number, author name, commit date and commit message.
         */
                SVNCommitInfo commitInfo = null;
                try {
                    commitInfo = addDir(editor, "svnCommit", "svnCommit/Test.txt", contents);
                } catch (SVNException e) {
                    e.printStackTrace();
                }
                System.out.println("The directory was added: " + commitInfo);
            }

    /*
     * This method performs commiting an addition of a  directory  containing  a
     * file.
     */
            /*
             * Display all tree of error messages.
             * Utility method SVNErrorMessage.getFullMessage() may be used instead of the loop.
             */

        });

        // Links to button clicks
        buttonDisconnect = (Button) findViewById(R.id.buttonDisconnect);

        // Capture button clicks

        buttonDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Application", "Disconnect");

                WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                wifi.setWifiEnabled(false); // true or false to activate/deactivate wifi

            }
        });
    }

    protected void onPause() {
        try {
            unregisterReceiver(wifiReceiver);
        }
        catch(IllegalArgumentException e) {
        }

        super.onPause();
    }

    protected void onResume() {
        registerReceiver(
                wifiReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        );
        super.onResume();
    }


    private class WifiScanReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiScanList = wifi.getScanResults();
            //xtWifiInfo.setText("");
            System.out.println("start scan");
            for (int i = 0; i < wifiScanList.size(); i++) {
                info = ((wifiScanList.get(i).SSID).toString());
                //Log.v("Info1", info);
                if (info.matches("TraceOp_.*")) {
                    ssid = info;
                    break;
                }
            }
            onPause();
            // Get the split of ssid

            parts =ssid.split("_");
            ssid_tmp =parts[1];

            // Discover the key

            try
            {
                hmac = calculateRFC2104HMAC(ssid_tmp, "Tr4ce0pe2018");
            }
            catch(SignatureException e)
            {
                e.printStackTrace();
            }
            catch (NoSuchAlgorithmException e)
            {
                e.printStackTrace();
            }
            catch(InvalidKeyException e)
            {
                e.printStackTrace();
            }

            Log.v("HMAC :",hmac);

            // Creation of WifiConfiguration instance
            String networkSSID = ssid;
            String networkPass = hmac;

            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID ="\""+networkSSID +"\"";   // Please note the quotes. String should contain ssid in quotes
            conf.preSharedKey ="\""+networkPass +"\"";

            wifi.addNetwork(conf);

            List<WifiConfiguration> list = wifi.getConfiguredNetworks();
            for(WifiConfiguration i :list )
            {
                if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                    wifi.disconnect();
                    wifi.enableNetwork(i.networkId, true);
                    wifi.reconnect();
                    break;
                }
            }
        }
    }
}

