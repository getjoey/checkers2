package com.joseph.boot.checkers;

import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class CheckersController {
	
	Checkers checkers = new Checkers();
	
	@RequestMapping("/")
	public String home() {
		return "CheckersView";
	}
	
	
	@PostMapping(value = "/movePlayer", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String processPlayerMove(@RequestBody String payload) throws Exception {
		
		String[] stringList = payload.split("&");
		String arg0 = stringList[0];
		String arg1 = stringList[1];
		String arg2 = stringList[2];
		String arg3 = stringList[3];
		int x1 = Integer.parseInt(arg0.substring(arg0.lastIndexOf("=")+1,arg0.length()));
		int y1 = Integer.parseInt(arg1.substring(arg1.lastIndexOf("=")+1,arg1.length()));
		int x2 = Integer.parseInt(arg2.substring(arg2.lastIndexOf("=")+1,arg2.length()));
		int y2 = Integer.parseInt(arg3.substring(arg3.lastIndexOf("=")+1,arg3.length()));
		
		String response = "failure";
		if(checkers.add(x1, y1, x2, y2)) {
			response = "success";
		}
		
		JSONObject data = new JSONObject();
		data.put("boardData", checkers.getBoard());
		data.put("response", response);

		return data.toString();
	}
	
	@RequestMapping(value = "/moveAi", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String doAiMove() {
		
		checkers.doBestMoveAI();
		JSONObject data = new JSONObject();
		data.put("boardData", checkers.getBoard());
		
		return data.toString();
	}
	
	@RequestMapping(value = "/getBoard", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String getBoard() {
		JSONObject data = new JSONObject();
		data.put("boardData", checkers.getBoard());
		return data.toString();
	}
	
	@RequestMapping(value = "/resetBoard", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String resetBoard() {
		JSONObject data = new JSONObject();
		checkers.resetBoard();
		data.put("boardData", checkers.getBoard());
		return data.toString();
	}
	
	

}
