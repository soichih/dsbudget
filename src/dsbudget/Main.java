package dsbudget;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import java.util.Properties;

import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.realm.MemoryRealm;
import org.apache.catalina.startup.Embedded;
import org.apache.tomcat.util.IntrospectionUtils;

import dsbudget.model.Budget;
import dsbudget.model.Page;

public class Main {
	
	static public String version = "2.0.7.2";
	
	private String path = null;
    public static Embedded tomcat = null;
    private Host host = null;
    private Context rootcontext;

    public static Properties conf;
    public static String port = "16091";

	public static void main(String[] args) {
		Main main = new Main();
		System.out.println("Starting dsBudget server " + Main.version);
		
		conf = new Properties();
		try {
			conf.load(new FileInputStream("dsbudget.conf"));
		} catch (FileNotFoundException e1) {
			System.out.println(e1.toString());
			System.exit(1);
		} catch (IOException e1) {
			System.out.println(e1.toString());
			System.exit(1);
		}
		
		try {
			main.startTomcat();
		} catch (LifecycleException e) {
			System.out.println(e.toString());
			System.exit(1);
		}
		System.out.println("Opening a browser...");
		BrowserControl.displayURL("http://localhost:"+main.port+"/dsbudget/main");
	}
		
    public void startTomcat() throws LifecycleException {
        Engine engine = null;

        System.setProperty("catalina.base", "tomcat");
        
        // Create an embedded server
        tomcat = new Embedded();
        tomcat.setCatalinaHome("tomcat");

        /*
        // set the memory realm
        MemoryRealm memRealm = new MemoryRealm();
        tomcat.setRealm(memRealm);
		*/
        
        // Create an engine
        engine = tomcat.createEngine();
        engine.setDefaultHost("localhost");

        // Create a default virtual host
        host = tomcat.createHost("localhost", "webapps");
        host.setAutoDeploy(false);
        engine.addChild(host);
  
        // Create the ROOT context
        /*
        rootcontext = tomcat.createContext("", "ROOT");
        rootcontext.setReloadable(true);
        rootcontext.addWelcomeFile("index.jsp");
        host.addChild(rootcontext);
		*/
        // create another application Context
        /*
        Context appCtx = this.embedded.createContext("/manager", "manager");
        appCtx.setPrivileged(true); 
        this.host.addChild(appCtx);
        */
        
        Context appCtx = tomcat.createContext("/dsbudget", "dsbudget");
        appCtx.setPrivileged(true); 
        host.addChild(appCtx);
        
        // Install the assembled container hierarchy
        tomcat.addEngine(engine);
        Connector connector = null;
        try {
            connector = new Connector();
            IntrospectionUtils.setProperty(connector, "address", "127.0.0.1");
            IntrospectionUtils.setProperty(connector, "port", port);     
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        connector.setEnableLookups(false);

        tomcat.addConnector(connector);
        tomcat.start();
    }
    
    public void stopTomcat() throws Exception {
    	tomcat.stop();
    }
    
    static public Page createEmptyPage(Budget budget) {
		Page page = new Page(budget);
		return page;
    }
}