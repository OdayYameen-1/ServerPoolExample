package com.oday.project.controller;



import java.util.ArrayList;
import java.util.Comparator;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.oday.project.configuration.AerospikeConfiguration;
import com.oday.project.model.Server;
import com.oday.project.model.ServerStatus;
import com.oday.project.repository.IncorrectVersion;
import com.oday.project.repository.ServerRepository;

@RestController
@ComponentScan(basePackageClasses = AerospikeConfiguration.class)
public class ServerController {
	@Autowired
	private ServerRepository serverRepository;
	int sum=0;
	private Thread t;
	private static final Object obj=new Object();

	@RequestMapping(value = "/getServer",headers = "Accept=application/json")
	public Iterable<Server> getServer() {

		return serverRepository.findAll();

	}
	@RequestMapping(value = "/size")
	public int size() {
		sum=0;
		serverRepository.findAll().forEach(s->{
			sum+=s.getCapacity();
			
			
			
			
		});
		return sum;
		
	}
	@RequestMapping(value = "/getMyServer/{id}",headers = "Accept=application/json")
	public Server getMyServer(@PathVariable(value  = "id") long id) {
		Server ser=serverRepository.findById(id).get();
		if(ser.getStatus()==ServerStatus.Active) {
			return ser;
		}else return null;
	}

	@RequestMapping(value = "/getString/{str}",headers = "Accept=application/json")
	public String getString(@PathVariable(value = "str")String str){
		return "{message:"+str+"}";

	}
	@RequestMapping(value = "/allocate/{capacity}/{nameOfUser}",headers = "Accept=application/json")
	public RedirectView allocate(@PathVariable(value = "capacity") int capacity,
								 @PathVariable(value = "nameOfUser") String nameOfUser) {
		allocateTheServerGraterThan100(capacity,nameOfUser);

		return new RedirectView("/getServer");

	}

	private synchronized void allocateTheServerGraterThan100(int capacity, String nameOfUser) {
		System.out.println(capacity+"          pppp");
		
		List <Server> servers=new ArrayList<>();
		serverRepository.findAll().forEach(servers::add);
			
		for (Server server:servers){
			if(server.getCapacity()<100){
				
				if(server.getCapacity()+capacity<=100){
					
					
						server.setCapacity(server.getCapacity()+capacity);
						capacity=0;	
						
					

				}else {
					int x = 100 - server.getCapacity();
					
						
						server.setCapacity(100);
						
						capacity = capacity - x;	
				
						
					
					
					

				}
				server.getMyUser().add(nameOfUser);
				server.setNoUser(server.getNoUser()+1);
				try {
					serverRepository.update(server);
					
				} catch (IncorrectVersion incorrectVersion) {
					System.err.println("eeeeeeeeeerrrrrrrrrroooooooooooorrrrrrrrrrrr");
				
					allocateTheServerGraterThan100(capacity,nameOfUser);
				}

if(capacity==0)
	return;



			}


		}

		if(capacity>0){


			long newid = (System.currentTimeMillis() << 20) | (System.nanoTime() & 0xFFFFFL);
			List<String> l = new ArrayList<String>();
			l.add("");
			Server nnsServer = new Server(newid, 0, ServerStatus.Createing, 0, l, 1);
			System.out.println("the server with id= " +nnsServer.getId()+" now is in = "+nnsServer.getStatus());
			
				serverRepository.save(nnsServer);

				activateServer(nnsServer);
				
				allocateTheServerGraterThan100(capacity,nameOfUser);
			
				


		}



	}

	public  void activateServer(Server s) {
		Server server=serverRepository.findById(s.getId()).get();
		Thread thread=new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(20000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				server.setStatus(ServerStatus.Active);
				
				try {
					serverRepository.update(server);
					System.out.println("the server with id= " +server.getId()+" now is in = "+server.getStatus());
				} catch (IncorrectVersion e) {
					
					System.err.println("cfrwqegwergh4564654egva4654vb64sa6b44465464");
					activateServer(server);
				}
				

			}
		});

		thread.start();

		
		
		
		
	}
}
/*
 * @RequestMapping(value = "/allocate/{capacity}/{nameOfUser}",headers =
 * "Accept=application/json") public RedirectView allocate(@PathVariable(value =
 * "capacity") int capacity,
 * 
 * @PathVariable(value = "nameOfUser") String nameOfUser) { if (capacity > 100)
 * return new RedirectView("go to : allocateG/{capacity}/{nameOfUser}");
 * 
 * allocateTheServerGraterThan100( capacity,nameOfUser); return new
 * RedirectView(string);
 * 
 * 
 * }
 */

/*
 * String allocateServer(int capacity, String nameOfUser) { List<Server> servers
 * = new ArrayList<Server>(); serverRepository.findAll().forEach(servers::add);
 * 
 * Server s = getBestServer(servers, capacity);
 * 
 * 
 * 
 * s.getMyUser().add(nameOfUser);
 * 
 * if (s.getNoUser() == 0) { s.setNoUser(s.getNoUser() + 1);
 * s.setCapacity(capacity);
 * 
 * } else { s.setCapacity(s.getCapacity() + capacity); s.setNoUser(s.getNoUser()
 * + 1); }
 * 
 * 
 * try { serverRepository.update(s); } catch (IncorrectVersion e) {
 * System.err.println("eeeeeeeeerrrrrrrrrrrrrroooooooooooorrrrrrr"); return
 * allocateServer(capacity, nameOfUser);
 * 
 * }
 * 
 * return "/getMyServer/"+s.getId();
 * 
 * 
 * }
 */

///////////////////////*///////////////////////////////*///////////////////////

/*
 * private Server getBestServer(List<Server> servers, int capacity) {
 * 
 * for (Server server : servers) { if (server.getNoUser() == 0) return server; }
 * List<Server> tofindMin = new ArrayList<Server>(); for (Server server :
 * servers) {
 * 
 * if (server.getCapacity() + capacity <= 100) { tofindMin.add(server);
 * 
 * }
 * 
 * } Optional<Server> theminServerCapacity = tofindMin.stream().min(new
 * Comparator<Server>() {
 * 
 * @Override public int compare(Server o1, Server o2) { if (o1.getCapacity() >
 * o2.getCapacity())
 * 
 * return 1;
 * 
 * else if (o1.getCapacity() < o2.getCapacity()) return -1;
 * 
 * else
 * 
 * return 0; }
 * 
 * });////// end of find min server capacity
 * 
 * if (!theminServerCapacity.isPresent()) {
 * 
 * long newid = (System.currentTimeMillis() << 20) | (System.nanoTime() &
 * 0xFFFFFL); List<String> l = new ArrayList<String>(); l.add(""); Server
 * nnsServer = new Server(newid, 0, ServerStatus.Createing, 0, l, 1);
 * System.out.println("the server with id= "
 * +nnsServer.getId()+" now is in = "+nnsServer.getStatus());
 * 
 * serverRepository.save(nnsServer);
 * 
 * 
 * activateServer(nnsServer);
 * 
 * return nnsServer; }
 * 
 * return theminServerCapacity.get();
 * 
 * }
 * 
 * }
 */
