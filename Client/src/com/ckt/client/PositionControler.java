package com.ckt.client;

public class PositionControler {
	private static final int MAX_LENGTH = 6;
	private int mPositionMap[][] = initMap();
	private ServerService mServerService;
	
	public PositionControler(ServerService serverService){
		mServerService = serverService;
		mPositionMap[0][0] = 1;
		mPositionMap[1][0] = 0;
	}
	
	private static int [][] initMap(){
		int [][] map = new int [MAX_LENGTH][MAX_LENGTH];
		for(int indexx = 0;indexx < MAX_LENGTH;indexx++){
			for(int indexy = 0;indexy < MAX_LENGTH;indexy++){
				map[indexx][indexy] = -1;
			}
		}
		return map;
	}
	public void changePosition(Client client,int positions[]){
		System.out.println("yadong code is here");
		int index = client.getmIndex();
		int [] indexs = getPositionOnMap(index);
		System.out.println("yadong code is here4"+indexs[0]+indexs[1]);
		if (null == indexs || indexs.length != 2)
			return;
		int cloume = indexs[0];
		int row = indexs[1];
		
		System.out.println("yadong code is here5 "+cloume+" "+row);
		int left = -1,top = -1,right = -1,bottom = -1;
		Client  horizontalClient = null,verticalClient = null;
		//left,right
		if(positions[0] < 0 && row > 0 ){
			System.out.println("yadong code is here5 "+cloume+" "+row);
			int clientIndex =  mPositionMap[cloume][row-1];
			//if the client on the left position do nothing
			if( clientIndex == -1){
				return;
			}else{
				System.out.println("yadong code is here 3");
				horizontalClient = mServerService.getClientByIndex(clientIndex);
				System.out.println("yadong code is here 2"+(null != horizontalClient));
				if(null != horizontalClient){
					int screenWidth = horizontalClient.getScreenWidth();
					left = screenWidth+positions[0];
					right = screenWidth+positions[2];
				}
			}
		} else if(positions[2] > client.getScreenWidth() && row < MAX_LENGTH-1){
			int clientIndex =  mPositionMap[cloume][row+1];
			//if the client on the right position do nothing
			if( clientIndex == -1){
				return;
			} else {
				horizontalClient = mServerService.getClientByIndex(clientIndex);
				if(null != horizontalClient){
					int screenWidth = client.getScreenWidth();
					right = positions[2] - screenWidth;
					left = 0-(screenWidth - positions[0]);
				}
			}
		}
		
		//top,bottom
		if(positions[1] < 0 && cloume >0){
			int clientIndex = mPositionMap[cloume-1][row];
			if( clientIndex == -1){
				return;
			} else {
				verticalClient = mServerService.getClientByIndex(clientIndex);
				if(null != verticalClient){
					int screenHeight = verticalClient.getScreenHight();
					top = screenHeight + positions[1];
					bottom = screenHeight + positions[3];
				}
			}
				
		} else if(positions[3] > client.getScreenHight() && cloume < MAX_LENGTH - 1){
			int clientIndex = mPositionMap[cloume+1][row];
			if(clientIndex == -1){
				return ;
			}else{
				verticalClient = mServerService.getClientByIndex(clientIndex);
				if(null != verticalClient){
					int screenHeight = client.getScreenHight();
					bottom = positions[3]-screenHeight;
					top = 0 - (screenHeight - positions[1]);
				}
			}
		}
		//TODO let verticalClient and horizontalClient move
		if(left != -1 && right !=-1){
			horizontalClient.sendMessage(Client.CLIENT_CHANGE_POSITION+left+"|"+positions[1]+"|"+right+"|"+positions[3]);
		}
		if(top!= -1 && bottom != -1){
			verticalClient.sendMessage(Client.CLIENT_CHANGE_POSITION+positions[0]+"|"+top+"|"+positions[2]+"|"+bottom);
		}
	}
	
	private int [] getPositionOnMap(int index){
		for(int i = 0;i < mPositionMap.length ;i++){
			for(int j = 0;j < mPositionMap[i].length;j++){
				if(mPositionMap[i][j] == index)
					return new int[]{i,j};
			}
		}
		return null;
	}

}
