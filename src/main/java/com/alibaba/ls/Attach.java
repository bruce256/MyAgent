package com.alibaba.ls;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

/**
 * @author 吕胜 lvheng1
 * @date 2022/4/17
 **/
public class Attach {
	
	public static void main(String[] args) {
		List<VirtualMachineDescriptor> list = VirtualMachine.list();
		System.out.println("jvm process id:");
		for (VirtualMachineDescriptor vmd : list) {
			System.out.println(vmd.id() + '\t' + vmd.displayName());
		}
		
		Scanner cin = new Scanner(System.in);
		int     pid = cin.nextInt();
		
		try {
			VirtualMachine virtualMachine = VirtualMachine.attach(String.valueOf(pid));
			virtualMachine.loadAgent("/Users/lvsheng1/study/github/MyAgent/target/MyAgent-1.0-SNAPSHOT-jar-with-dependencies.jar");
			System.out.println("ok");
			
			cin.nextInt();
			//virtualMachine.detach();
		} catch (AttachNotSupportedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (AgentLoadException e) {
			e.printStackTrace();
		} catch (AgentInitializationException e) {
			e.printStackTrace();
		}
	}
}
