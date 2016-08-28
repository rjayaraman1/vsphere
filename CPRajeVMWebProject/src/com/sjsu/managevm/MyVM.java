package com.sjsu.managevm;

import com.vmware.vim25.mo.*;
import com.vmware.vim25.*;

import java.net.URL;
import java.text.SimpleDateFormat;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.PerfEntityMetric;
import com.vmware.vim25.PerfEntityMetricBase;
import com.vmware.vim25.PerfEntityMetricCSV;
import com.vmware.vim25.PerfMetricId;
import com.vmware.vim25.PerfMetricIntSeries;
import com.vmware.vim25.PerfMetricSeries;
import com.vmware.vim25.PerfMetricSeriesCSV;
import com.vmware.vim25.PerfProviderSummary;
import com.vmware.vim25.PerfQuerySpec;
import com.vmware.vim25.PerfSampleInfo;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.PerformanceManager;
import com.vmware.vim25.mo.ServiceInstance;

public class MyVM
{
    // instance variables - replace the example below with your own
    public String vmname ;
    public ServiceInstance si ;
    public VirtualMachine vm ;

    public static void PowerCycleTest()
    {
        MyVM myvm = new MyVM( Config.getVmwareVM() ) ;
        System.out.println( "VMware VM: " + Config.getVmwareVM() ) ;
        myvm.powerOff() ;
        myvm.reset() ;
        myvm.powerOn() ;
        myvm.standBy();
    }

    public static void shutdownAll() throws Exception
    {
        ServiceInstance si = new ServiceInstance
            (new URL(Config.getVmwareHostURL()), Config.getVmwareLogin(), Config.getVmwarePassword(), true);
        Folder rootFolder = si.getRootFolder();
        ManagedEntity[] vms = new InventoryNavigator(rootFolder).searchManagedEntities(
                new String[][] { {"VirtualMachine", "name" }, }, true);
        VirtualMachine vm ;
        for(int i=0; i<vms.length; i++)
        {
            vm = (VirtualMachine) vms[i] ;
            VirtualMachineRuntimeInfo vmri = vm.getRuntime();
            VirtualMachinePowerState powerstate = vmri.getPowerState();            
            System.out.println( vm.getName() + " => " + powerstate.toString());
            if ( powerstate.toString().equals( "poweredOn" ) )
            {
                Task task = vm.powerOffVM_Task();
                if(task.waitForMe()==Task.SUCCESS)
                {
                    System.out.println( vm.getName() + " Powered off");
                }
            }
            if ( powerstate.toString().equals( "suspended" ) )
            {
                Task task1 = vm.powerOnVM_Task(null);
                if(task1.waitForMe()==Task.SUCCESS)
                {
                    System.out.println(  vm.getName() + " Powered on");
                }
                Task task2 = vm.powerOffVM_Task();
                if(task2.waitForMe()==Task.SUCCESS)
                {
                    System.out.println( vm.getName() + " Powered off");
                }
            }
        }        
    }

    public String HostSummary() throws Exception
    {
    	 StringBuilder sb = new StringBuilder();
        // setup service instance and handle on root folder
        ServiceInstance si = new ServiceInstance(
                new URL(Config.getVmwareHostURL()), 
                Config.getVmwareLogin(), 
                Config.getVmwarePassword(), true);

        // get reference to root folder
        Folder rootFolder = si.getRootFolder();

        // setup an inventory navigator
        InventoryNavigator nav = new InventoryNavigator(rootFolder) ;

        System.out.println("\n============ Hosts ============");
        sb.append("\n============ Hosts ============");
        ManagedEntity[] hosts = nav.searchManagedEntities(new String[][] { {"HostSystem", "name" }, }, true);

        HostSystem host = null ;
        for(int i=0; i<hosts.length; i++)
        {
            System.out.println(" host["+i+"]= " + hosts[i].getName());
            sb.append("\n host["+i+"]= " + hosts[i].getName());
            host = (HostSystem) hosts[i] ;

            // print out hardware information
            HostListSummary hls = host.getSummary();
            HostHardwareSummary hw = hls.getHardware();

            System.out.format( "Manufacturer: %s\n", hw.getVendor() ) ;
            sb.append("\n Manufacturer: "+hw.getVendor());
            System.out.format("Model: %s\n", hw.getModel() ) ; 
            sb.append("\n Model: "+hw.getModel());
            HostListSummaryQuickStats qstats = hls.getQuickStats();
            System.out.println("Distributed CPU Fairness = "+qstats.getDistributedCpuFairness());
            sb.append("\n Distributed CPU Fairness = "+qstats.getDistributedCpuFairness());
            System.out.println("\nDistributed Memory Fairness = "+qstats.getDistributedMemoryFairness());
            sb.append("\n Distributed Memory Fairness = "+qstats.getDistributedMemoryFairness());
            System.out.println("Aggregated CPU Usage Accross All Cores (in MHz) = "+qstats.getOverallCpuUsage());
            sb.append("\n Aggregated CPU Usage Accross All Cores (in MHz) = "+qstats.getOverallCpuUsage());
            System.out.println("Physical Memory Usage (MB) = "+qstats.getOverallMemoryUsage());
            sb.append("\n Physical Memory Usage (MB) = "+qstats.getOverallMemoryUsage());
            System.out.println("Speed of CPU Cores (Mhz) = "+hw.getCpuMhz());
            sb.append("\n Speed of CPU Cores (Mhz) = "+hw.getCpuMhz());
            System.out.println("Physical Memory (Bytes) = "+hw.getMemorySize());
            sb.append("\nPhysical Memory (Bytes) = "+hw.getMemorySize());
            System.out.println("Number of CPU Cores = "+hw.getNumCpuCores()); 
            sb.append("\nNumber of CPU Cores = "+hw.getNumCpuCores());
            System.out.format("CPU Capacity = %d x %.4f GHz\n",hw.getNumCpuCores(), (float)hw.getCpuMhz()/1000 );
            sb.append("\n CPU Capacity(GHz)"+hw.getNumCpuCores()+(float)hw.getCpuMhz()/1000);
            System.out.format("Memory Capacity = %.2f MB\n", (float)(hw.getMemorySize()/(1024*1024)) );       
            sb.append("\nMemory Capacity(MB)\n"+(float)(hw.getMemorySize()/(1024*1024))) ;
            System.out.println( "\n" ) ;
            sb.append("\n");
        }
		return(sb.toString());        

    }    

    public String showVMInfo()
    {
        System.out.println("\n============ Virtual Machine Info ============");
        StringBuilder sb = new StringBuilder();
        if ( vm != null )
        {
            VirtualMachineConfigInfo vminfo = vm.getConfig();
            VirtualMachineCapability vmc = vm.getCapability();
            //Added by Rajeswari J on Sep 8 2013
            System.out.println("CPU allocation limit" +vminfo.cpuAllocation.getLimit());
            sb.append("CPU allocation limit" +vminfo.cpuAllocation.getLimit()+"\n");
            System.out.println("VM: " + vm.getName());
            sb.append("VM Name: " + vm.getName()+"\n");
            System.out.println("GuestOS: " + vminfo.getGuestFullName());
            sb.append("GuestOS: " + vminfo.getGuestFullName()+"\n");
            System.out.println("Multiple snapshot supported: " + vmc.isMultipleSnapshotsSupported());
            sb.append("Multiple snapshot supported: " + vmc.isMultipleSnapshotsSupported()+"\n  ");
            VirtualMachineSummary vmsum = vm.getSummary() ;
            sb.append("VM Machine summary "+vmsum+"\n");
            VirtualMachineQuickStats vmstats = vmsum.getQuickStats() ;
            sb.append("VM QuickStats\n"+ vmsum.getQuickStats()+"\n");
            System.out.printf( "Guest Memory Usage: %d MB\n", vmstats.getGuestMemoryUsage() ) ;
            sb.append("Guest Memory Usage: MB\n"+vmstats.getGuestMemoryUsage()+"\n");
            System.out.printf("Host Memory Usage: %d MB\n",vmstats.getHostMemoryUsage()) ;
            sb.append("Host Memory Usage:  MB\n"+ vmstats.getHostMemoryUsage());
            System.out.printf( "Overall CPU Usage: %dMHz\n",vmstats.getOverallCpuUsage() ) ;
            sb.append("Overall CPU Usage: MHz\n"+vmstats.getOverallCpuUsage()+"\n");
            VirtualMachineRuntimeInfo runtime = vmsum.getRuntime();
            sb.append("Virtual Machine Run time information"+runtime+"\n");
            SimpleDateFormat df = new SimpleDateFormat() ;
            System.out.println("Boot  time "+df);
            //System.out.println( "VM Connection State: " + runtime.getConnectionState() ) ;
           // System.out.printf( "Suspend Interval: %d\n", runtime.getSuspendInterval() ) ;
            System.out.println( "Suspend Time: " + runtime.getSuspendTime()) ;
            sb.append("Suspend Time " +runtime.getSuspendTime());
        }    
        return(sb.toString());
    }

    public String monitorCPU() throws Exception
    {
        PerformanceManager perfMgr = si.getPerformanceManager();
        StringBuilder sb = new StringBuilder();
        //Added  by Rajeswari J on Sep 3 2013
     //   VirtualDiskManager vdiskmgr = si.getVirtualDiskManager();
       
        int theKey = 0 ;
        System.out.println("Monitoring CPU");
        sb.append("\nMonitoring CPU");
        PerfCounterInfo[] pcis = perfMgr.getPerfCounter();
        
        for(int i=0; pcis!=null && i<pcis.length; i++)
        {
            String perfCounter = pcis[i].getGroupInfo().getKey() + "."
                + pcis[i].getNameInfo().getKey() + "." 
                + pcis[i].getRollupType();
             
            if ( "cpu.usage.average".equals( perfCounter ) )
            {
                theKey = pcis[i].getKey() ;
                System.out.println( "\n" ) ;
                sb.append("\n");
                System.out.println("Key:" + pcis[i].getKey());
                sb.append("\nKey:" + pcis[i].getKey());
                System.out.println("PerfCounter:" + perfCounter);
                sb.append("\nPerfCounter:" + perfCounter);
                System.out.println("Level:" + pcis[i].getLevel());
                sb.append("\nLevel:" + pcis[i].getLevel());
                System.out.println("StatsType:" + pcis[i].getStatsType());
                sb.append("\nStatsType:" + pcis[i].getStatsType());
                System.out.println("UnitInfo:" + pcis[i].getUnitInfo().getKey());  
                sb.append("\nUnitInfo:" + pcis[i].getUnitInfo().getKey());
                sb.append("\n");
            }

        } 

        // find out the refresh rate for the virtual machine
        PerfProviderSummary pps = perfMgr.queryPerfProviderSummary(vm);
        int refreshRate = pps.getRefreshRate().intValue();

        // retrieve all the available perf metrics for vm
        PerfMetricId[] pmis = perfMgr.queryAvailablePerfMetric(
                vm, null, null, refreshRate);

        PerfQuerySpec qSpec = createPerfQuerySpec(
                vm, pmis, 3, refreshRate);

        while(true) 
        {
            showVMInfo();

            PerfQuerySpec[ ] qspecs = new PerfQuerySpec[ ] {qSpec} ;
            PerfEntityMetricBase[ ] pValues = perfMgr.queryPerf( qspecs );

            if(pValues != null)
            {
                for(int i=0; i<pValues.length; ++i) 
                {
                    String entityDesc = pValues[i].getEntity().getType() + ":" + pValues[i].getEntity().get_value();
                    System.out.println("\nEntity:" + entityDesc);
                    sb.append("\nEntity:" + entityDesc);

                    if( pValues[i] instanceof PerfEntityMetricCSV)
                    {
                        PerfMetricSeriesCSV[] csvs =  ((PerfEntityMetricCSV)pValues[i]).getValue();
                        for(int j=0; j<csvs.length; j++)
                        {
                            if ( csvs[j].getId().getCounterId() == theKey )
                            {
                                System.out.println("\nPerfCounterId:" + csvs[j].getId().getCounterId());
                                sb.append("\nPerfCounterId:" + csvs[j].getId().getCounterId());
                                System.out.println("Sample values:" + csvs[j].getValue());
                                sb.append("Sample values:" + csvs[j].getValue());
                            }
                        }
                    }
                    else
                    {
                        System.out.println("UnExpected sub-type of PerfEntityMetricBase.");
                    }
                } 
                //Above brace to end for
            }
            //above brace for if not null
            System.out.println("Sleeping xx seconds...");
          //  Thread.sleep(refreshRate*3*1000);
            Thread.sleep(refreshRate*3*100);
            return(sb.toString());
        }
    
       //above brace for while (true)
    }
    public String cloneVM( String clone_name ) 
    {
    	 StringBuilder sb = new StringBuilder();
    	VirtualMachineCloneSpec cloneSpec = new VirtualMachineCloneSpec();
        cloneSpec.setLocation(new VirtualMachineRelocateSpec());
        cloneSpec.setPowerOn(true);
        cloneSpec.setTemplate(false);

        try {
            Task task = vm.cloneVM_Task((Folder) vm.getParent(), clone_name, cloneSpec);
            System.out.println("Launching the VM clone task. Please wait ...") ;
            sb.append("\nLaunching the VM clone task. Please wait..");

            String status = task.waitForMe();
            if(status==Task.SUCCESS)
            {
                System.out.println("Cloned successfully.");
                sb.append("Cloned successfully..");
            }
            else
            {
                System.out.println("Failure! VM cannot be cloned");
                sb.append("Failure! VM cannot be cloned");
            }
        } catch ( Exception e ) 
        { System.out.println( e.toString() ) ; }
        return(sb.toString());
    }

    public void coldMigrateVM( String new_host )
    {
        Folder rootFolder = si.getRootFolder();
        System.out.println("\nCold Migrating... " );        

        try {
            
            suspend() ; // suspend vm if needed

            HostSystem host = (HostSystem) new InventoryNavigator(rootFolder).searchManagedEntity("HostSystem", new_host);
            System.out.println("Migrate To Host: " + host.getName());

            HostDatastoreBrowser hdb = host.getDatastoreBrowser();
            System.out.println("datastores:");
            Datastore[] ds = hdb.getDatastores();
            for(int i=0; ds!=null && i<ds.length; i++)
            {
                System.out.println("  datastore["+i+"]:");
                DatastoreInfo di = ds[i].getInfo();
                System.out.println("  Name:" + di.getName());
                System.out.println("  FreeSpace:" + di.getFreeSpace());
                System.out.println("  MaxFileSize:" + di.getMaxFileSize());
            }
            System.out.println("Migrate To Datastore: " + ds[0].getName());

            ResourcePool rp = null ;
            ManagedEntity [] rps = new InventoryNavigator(rootFolder).searchManagedEntities( "ResourcePool" ) ; 
            int poolIndex = 0;
            for(int i=0; i<rps.length; i++)
            {
                System.out.format( "  [%d] => %s\n", i, rps[i].getName() ) ;
                if ( rps[i].getName().equals( new_host ) )
                    poolIndex = i ;
            }     
            System.out.println("Migrate To ResourcePool: " + rps[poolIndex].getName());

            VirtualMachineRelocateSpec spec = new VirtualMachineRelocateSpec() ;
            spec.setHost( host.getMOR() ) ;
            spec.setDatastore( ds[0].getMOR() ) ;
            spec.setPool( rps[poolIndex].getMOR() ) ;

            Task task = vm.relocateVM_Task( spec ) ;
            if(task.waitForMe()==Task.SUCCESS)
            {
                System.out.println("VMotioned!");
            }
            else
            {
                System.out.println("VMotion failed!");
                TaskInfo info = task.getTaskInfo();
                System.out.println(info.getError().getFault());
            }        

            powerOn() ;
        } catch ( Exception e ) 
        { System.out.println( e.toString() ) ; }

    }

    public void liveMigrateVM( String new_host )
    {
        Folder rootFolder = si.getRootFolder();      
        System.out.println("\nLive Migrating VM (VMotion) to Host: " + new_host );

        try{

            HostSystem newHost = (HostSystem) new InventoryNavigator(rootFolder).searchManagedEntity( "HostSystem", new_host);
            ComputeResource cr = (ComputeResource) newHost.getParent();            

            String[] checks = new String[] {"cpu", "software"};
            HostVMotionCompatibility[] vmcs = si.queryVMotionCompatibility(vm, new HostSystem[] {newHost},checks );
            String[] comps = vmcs[0].getCompatibility();
            if(checks.length != comps.length)
            {
                System.out.println("CPU/software NOT compatible. Exit.");
                return;
            }

            Task task = vm.migrateVM_Task(
                    cr.getResourcePool(), 
                    newHost,
                    VirtualMachineMovePriority.highPriority, 
                    VirtualMachinePowerState.poweredOn);

            if(task.waitForMe()==Task.SUCCESS)
            {
                System.out.println("VMotioned!");
            }
            else
            {
                System.out.println("VMotion failed!");
                TaskInfo info = task.getTaskInfo();
                System.out.println(info.getError().getFault());
            }        
        } catch ( Exception e ) 
        { System.out.println( e.toString() ) ; }
    }

    /**
     * Constructor for objects of class MyVM
     */
    public MyVM( String vmname ) 
    {
        // initialise instance variables
        try {
            this.vmname = vmname ;
            this.si = new ServiceInstance
            (new URL(Config.getVmwareHostURL()), Config.getVmwareLogin(), Config.getVmwarePassword(), true);
            Folder rootFolder = si.getRootFolder();
            this.vm = (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine", this.vmname);
            showPowerState();
        } catch ( Exception e ) 
        { System.out.println( e.toString() ) ; }

        if( this.vm==null)
        {
            System.out.println("No VM " + vmname + " found");
            if ( this.si != null)
                this.si.getServerConnection().logout();
        }
    }

    public MyVM()
    {
        this(Config.getVmwareVM());
    }

    /**
     * Destructor for objects of class MyVM
     */
    protected void finalize() throws Throwable
    {
        this.si.getServerConnection().logout(); //do finalization here
        super.finalize(); //not necessary if extending Object.
    } 

    //Modified the return parameter from void to string by Rajeswari Jayaraman on Sep 7 2014
    public String showPowerState()
    {
        VirtualMachineRuntimeInfo vmri = vm.getRuntime();
        VirtualMachinePowerState powerstate = vmri.getPowerState();
        System.out.println( "VM Power State: " + powerstate.toString() ) ; 
        return powerstate.toString();
        
    }

    /**
     * Power On the Virtual Machine
     */
    public String powerOn() 
    {
    	StringBuilder sb = new StringBuilder();
        try {
          
            System.out.println("command selected  : power on ");
            sb.append("command selected: power on ");
           // showPowerState();
            if (showPowerState() == "poweredOff" || showPowerState() =="suspended")
               {
                 Task task = vm.powerOnVM_Task(null);
                 if(task.waitForMe()==Task.SUCCESS)
                 {
                    System.out.println(vmname + " powered on success");
                    sb.append(vmname + " powered on successfully");
                 }
                else 
                  { 
            	     sb.append(vmname+"Power on Failed");
                   }
               } 
            else {
            	 sb.append(vmname+"Invalid State");
                 }
          
               } catch ( Exception e ) 
               { System.out.println( e.toString() ) ;
                  return "Exception Condition";
               }
        return sb.toString();
    }

    /**
     * Power Off the Virtual Machine
     */
    public String powerOff() 
    {
    	StringBuilder sb = new StringBuilder();
        try {
            System.out.println("\n=========================");
            sb.append("Command selected: power off ");
            System.out.println("command: power off");
            showPowerState();
            Task task = vm.powerOffVM_Task();
            if(task.waitForMe()==Task.SUCCESS)
            {
                System.out.println(vmname + " powered off success");
                sb.append(vmname + "    powered off successfully");
            }
            showPowerState();
        } catch ( Exception e ) 
        { System.out.println( e.toString() ) ;
        return "Not a Valid state";
        }
        return sb.toString();
    }

    public void forcePowerOff()
    {
        try {
            VirtualMachineRuntimeInfo vmri = vm.getRuntime();
            VirtualMachinePowerState powerstate = vmri.getPowerState();            
            System.out.println( vm.getName() + " => " + powerstate.toString());
            if ( powerstate.toString().equals( "poweredOn" ) )
            {
                Task task = vm.powerOffVM_Task();
                if(task.waitForMe()==Task.SUCCESS)
                {
                    System.out.println( vm.getName() + " Powered off");
                }
            }
            if ( powerstate.toString().equals( "suspended" ) )
            {
                Task task1 = vm.powerOnVM_Task(null);
                if(task1.waitForMe()==Task.SUCCESS)
                {
                    System.out.println(  vm.getName() + " Powered on");
                }
                Task task2 = vm.powerOffVM_Task();
                if(task2.waitForMe()==Task.SUCCESS)
                {
                    System.out.println( vm.getName() + " Powered off");
                }
            }            
            showPowerState();
        } catch ( Exception e ) 
        { System.out.println( e.toString() ) ; }        
    }

    /**
     * Reset the Virtual Machine
     */

    public void reset() 
    {
        try {
            System.out.println("\n=========================");
            System.out.println("command: reset");
            showPowerState();
            Task task = vm.resetVM_Task();
            if(task.waitForMe()==Task.SUCCESS)
            {
                System.out.println(vmname + " reset");
            }
            showPowerState();
        } catch ( Exception e ) 
        { System.out.println( e.toString() ) ; }
    }

    /**
     * Suspend the Virtual Machine
     */

    public String suspend() 
    {
    	StringBuilder sb = new StringBuilder();
        try {
            System.out.println("\n=========================");
            System.out.println("command: suspend");
            showPowerState();
            Task task = vm.suspendVM_Task();
            if(task.waitForMe()==Task.SUCCESS)
            {
                System.out.println(vmname + " suspended");
                sb.append(vmname + " Suspended successfully");
            }
            showPowerState();
          } 
          catch ( Exception e ) 
        
           { 
        	System.out.println( e.toString() ) ; 
        	return "Issues with Suspend operation";
        	
           }
          
          
        return sb.toString();
    }

    /**
     *  Put VM & Guest OS on Standby
     *  (requires VMWare Tools Installation)
     */
    public void standBy() 
    {
        try {
            System.out.println("\n=========================");
            System.out.println("command: stand by");
            showPowerState();
            vm.standbyGuest();
            System.out.println(vmname + " guest OS stoodby");
            showPowerState();
        } catch ( Exception e ) 
        { System.out.println( e.toString() ) ; }
    }

    /**
     * Performance Metadata Utility Functions
     */

    public static void PerfCounters() throws Exception
    {

        ServiceInstance si = new ServiceInstance(
                new URL(Config.getVmwareHostURL()), Config.getVmwareLogin(), Config.getVmwarePassword(), true);

        PerformanceManager perfMgr = si.getPerformanceManager();

        System.out.println("***Print All Descriptions:");
        PerformanceDescription pd = perfMgr.getDescription();
        printPerfDescription(pd);

        System.out.println("\n***Print All Historical Intervals:");
        PerfInterval[] pis = perfMgr.getHistoricalInterval();
        printPerfIntervals(pis);

        System.out.println("\n***Print All Perf Counters:");
        PerfCounterInfo[] pcis = perfMgr.getPerfCounter();
        printPerfCounters(pcis);

        si.getServerConnection().logout();
    }

    private static void printPerfIntervals(PerfInterval[] pis)
    {
        for(int i=0; pis!=null && i<pis.length; i++)
        {
            System.out.println("\nPerfInterval # " + i);
            StringBuffer sb = new StringBuffer();
            sb.append("Name:" + pis[i].getName());
            sb.append("\nKey:" + pis[i].getKey());
            sb.append("\nLevel:"+ pis[i].getLevel());
            sb.append("\nSamplingPeriod:" + pis[i].getSamplingPeriod());
            sb.append("\nLength:" + pis[i].getLength());
            sb.append("\nEnabled:" + pis[i].isEnabled());
            System.out.println(sb);
        }
    }

    private static void printPerfCounters(PerfCounterInfo[] pcis)
    {
        for(int i=0; pcis!=null && i<pcis.length; i++)
        {
            System.out.println("\nKey:" + pcis[i].getKey());
            String perfCounter = pcis[i].getGroupInfo().getKey() + "."
                + pcis[i].getNameInfo().getKey() + "." 
                + pcis[i].getRollupType();
            System.out.println("PerfCounter:" + perfCounter);
            System.out.println("Level:" + pcis[i].getLevel());
            System.out.println("StatsType:" + pcis[i].getStatsType());
            System.out.println("UnitInfo:" 
                + pcis[i].getUnitInfo().getKey());
        }
    }

    private static void printPerfDescription(PerformanceDescription pd)
    {
        ElementDescription[] eds = pd.getCounterType();
        printElementDescriptions(eds);

        ElementDescription[] statsTypes = pd.getStatsType();
        printElementDescriptions(statsTypes);
    }

    private static void printElementDescriptions(ElementDescription[] eds)
    {
        for(int i=0; eds!=null && i<eds.length; i++)
        {
            printElementDescription(eds[i]);
        }
    }

    private static void printElementDescription(ElementDescription ed)
    {
        System.out.println("\nKey:" + ed.getKey());
        System.out.println("Label:" + ed.getLabel());
        System.out.println("Summary:" + ed.getSummary());
    }    

    private static PerfQuerySpec createPerfQuerySpec(ManagedEntity me, 
    PerfMetricId[] metricIds, int maxSample, int interval)
    {
        PerfQuerySpec qSpec = new PerfQuerySpec();
        qSpec.setEntity(me.getMOR());
        // set the maximum of metrics to be return
        // only appropriate in real-time performance collecting
        qSpec.setMaxSample(new Integer(maxSample));
        //    qSpec.setMetricId(metricIds);
        // optionally you can set format as "normal"
        qSpec.setFormat("csv");
        // set the interval to the refresh rate for the entity
        qSpec.setIntervalId(new Integer(interval));

        return qSpec;
    }

    private static void displayValues(PerfEntityMetricBase[] values)
    {
        for(int i=0; i<values.length; ++i) 
        {
            String entityDesc = values[i].getEntity().getType() 
                + ":" + values[i].getEntity().get_value();
            System.out.println("Entity:" + entityDesc);
            if(values[i] instanceof PerfEntityMetric)
            {
                printPerfMetric((PerfEntityMetric)values[i]);
            }
            else if(values[i] instanceof PerfEntityMetricCSV)
            {
                printPerfMetricCSV((PerfEntityMetricCSV)values[i]);
            }
            else
            {
                System.out.println("UnExpected sub-type of " +
                    "PerfEntityMetricBase.");
            }
        }
    }

    private static void printPerfMetric(PerfEntityMetric pem)
    {
        PerfMetricSeries[] vals = pem.getValue();
        PerfSampleInfo[]  infos = pem.getSampleInfo();

        System.out.println("Sampling Times and Intervales:");
        for(int i=0; infos!=null && i <infos.length; i++)
        {
            System.out.println("Sample time: " 
                + infos[i].getTimestamp().getTime());
            System.out.println("Sample interval (sec):" 
                + infos[i].getInterval());
        }
        System.out.println("Sample values:");
        for(int j=0; vals!=null && j<vals.length; ++j)
        {
            System.out.println("Perf counter ID:" 
                + vals[j].getId().getCounterId());
            System.out.println("Device instance ID:" 
                + vals[j].getId().getInstance());

            if(vals[j] instanceof PerfMetricIntSeries)
            {
                PerfMetricIntSeries val = (PerfMetricIntSeries) vals[j];
                long[] longs = val.getValue();
                for(int k=0; k<longs.length; k++) 
                {
                    System.out.print(longs[k] + " ");
                }
                System.out.println("Total:"+longs.length);
            }
            else if(vals[j] instanceof PerfMetricSeriesCSV)
            { // it is not likely coming here...
                PerfMetricSeriesCSV val = (PerfMetricSeriesCSV) vals[j];
                System.out.println("CSV value:" + val.getValue());
            }
        }
    }

    private static void printPerfMetricCSV(PerfEntityMetricCSV pems)
    {
        System.out.println("SampleInfoCSV:" 
            + pems.getSampleInfoCSV());
        PerfMetricSeriesCSV[] csvs = pems.getValue();
        for(int i=0; i<csvs.length; i++)
        {
            System.out.println("PerfCounterId:" 
                + csvs[i].getId().getCounterId());
            System.out.println("CSV sample values:" 
                + csvs[i].getValue());
        }
    }    

}
