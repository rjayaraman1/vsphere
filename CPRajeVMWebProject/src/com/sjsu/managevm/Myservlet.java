package com.sjsu.managevm;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Author: Rajeswari Jayaraman
 * This program gets handle of VMware API objects
 * This program manages the VM that we have created in SJSU VMlab.
 * The VM name, VM host name are hard coded in the program
 */
import javax.swing.JFrame;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;


import javax.swing.ImageIcon;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;



import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

/**
 * Servlet implementation class Myservlet
 */
public class Myservlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Myservlet() {
        super();
  
 	//Added by Rajeswari J to get host information
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Added by Rajeswari J
		PrintWriter out = response.getWriter();
		String option = request.getParameterValues("option")[0];
	 	 final MyVM RajeVM = new MyVM("Rxxxxxxx@sjsu.edu");
	  	System.out.println( "VMware VM: " + Config.getVmwareVM() ) ;
	  	System.out.println("Current State of the VM "+RajeVM.showPowerState());
	  	System.out.println("Option selected= "+option);
		//out.print("Hello Raje..Welcome to Servlet 233233 : " + request.getParame;terValues("option")[0]);
		switch(option){
		case "poweron" : {  
			 
            	      if (RajeVM.showPowerState() == "poweredOff" || RajeVM.showPowerState() =="suspended") 
            	      {
            		     out.print(RajeVM.powerOn());
            	      }
	                 else
	                 {
	            	  System.out.println("VM rajeswari_jayaraman@sjsu.edu can not be switched on");
	            	  out.print("VM rajeswari_jayaraman@sjsu.edu can not be switched on");
	            	 }
			        break;
		           }
		case "poweroff": {
			      System.out.println("I am in case 2");
			      if (RajeVM.showPowerState() != "poweredOff" ) 
			      {
        		       out.print(RajeVM.powerOff());
        	      }
                 else 
                 {
            	   System.out.println("VM rajeswari_jayaraman@sjsu.edu can not be switched off");
            	   out.print("VM rajeswari_jayaraman@sjsu.edu can not be switched off");
                 }
		     	break;
		      }
		case "suspend": {
			 System.out.println("I am in case 3");
		      if (RajeVM.showPowerState() == "poweredOn" ) 
		      {
   		       out.print(RajeVM.suspend());
		      }
   	      
            else 
            {
       	     System.out.println("VM rajeswari_jayaraman@sjsu.edu can not be suspended");
       	     out.print("VM rajeswari_jayaraman@sjsu.edu can not be suspened");
           }
	     	break;
			
		     }
		case "viewdetails": {
			System.out.println("I am in case 4 vmdeails");
			out.print(RajeVM.showVMInfo());
			break;
		}
		case "monitor": {
			 if (RajeVM.showPowerState() == "poweredOn")
			 {
				try {
					out.print(RajeVM.HostSummary());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				 try {
						out.print(RajeVM.monitorCPU());
					    } catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					   }
			 }
			else 
			{
	           System.out.println("Can not get Live performance statistics.. Pl switch it on..");
	           out.print("Can not get Live performance statistics.. Pl switch it on..");
			}
		
		break;
		}
		case "exit": 
		{
			out.print("Good Bye");
			break;
		}
		case "Clone":
		{
			if (RajeVM.showPowerState() == "poweredOn")
		      out.print(RajeVM.cloneVM("Rajeclone2"));
			else
			{
				System.out.println("VM rajeswari_jayaraman@sjsu.edu can not be cloned. Pl switch it on..");
				out.print("VM rajeswari_jayaraman@sjsu.edu can not be cloned. Pl switch it on.");
			}
		}
		}
}
		
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
