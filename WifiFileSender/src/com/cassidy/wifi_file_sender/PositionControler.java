package com.cassidy.wifi_file_sender;


public class PositionControler {
	private static final int MIN_NOTIFICATION = 50;
	private static final int MAX_LENGTH = 6;
	private int mPositionMap[][] = initMap();
	private boolean mChanged[][] = new boolean [MAX_LENGTH][MAX_LENGTH];
	private ServerService mServerService;
	
	public PositionControler(ServerService serverService){
		mServerService = serverService;
		mPositionMap[0][0] = 0;
		mPositionMap[0][1] = 1;
		mPositionMap[1][1] = 2;
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

	public void changePosition(Client client,int positions[],boolean isFromUi){
		int index = client.getmIndex();
		int [] indexs = getPositionOnMap(index);
		if(isFromUi){
			markChanged(indexs);
		}
		if (null == indexs || indexs.length != 2)
			return;
		int cloume = indexs[0];
		int row = indexs[1];
		
		int left = -1,top = -1,right = -1,bottom = -1;
		Client  horizontalClient = null,verticalClient = null;
		//left,right
		if(positions[0] < MIN_NOTIFICATION && row > 0 ){
			int clientIndex =  mPositionMap[cloume][row-1];
			//if the client on the left position do nothing
			if( clientIndex == -1){
				return;
			}else{
				horizontalClient = mServerService.getClientByIndex(clientIndex);
				if(null != horizontalClient && !isChanged(new int[]{cloume,row - 1})){
					System.out.println("yadong changed -- "+cloume+" || "+(row-1));
					int screenWidth = horizontalClient.getScreenWidth();
					left = screenWidth+positions[0];
					right = screenWidth+positions[2];
				}
			}
		} else if(positions[2] > client.getScreenWidth()-MIN_NOTIFICATION && row < MAX_LENGTH-1){
			int clientIndex =  mPositionMap[cloume][row+1];
			//if the client on the right position do nothing
			if( clientIndex == -1){
				return;
			} else {
				horizontalClient = mServerService.getClientByIndex(clientIndex);
				if(null != horizontalClient && !isChanged(new int[]{cloume,row+1}) ){
					System.out.println("yadong changed -- "+cloume+" || "+(row+1));
					int screenWidth = client.getScreenWidth();
					right = positions[2] - screenWidth;
					left = 0-(screenWidth - positions[0]);
				}
			}
		}
		
		//top,bottom
		if(positions[1] < MIN_NOTIFICATION && cloume >0){
			int clientIndex = mPositionMap[cloume-1][row];
			if( clientIndex == -1){
				return;
			} else {
				verticalClient = mServerService.getClientByIndex(clientIndex);
				if(null != verticalClient && !isChanged(new int[]{cloume-1,row})){
					System.out.println("yadong changed -- "+(cloume-1)+" || "+row);
					int screenHeight = verticalClient.getScreenHight();
					top = screenHeight + positions[1];
					bottom = screenHeight + positions[3];
				}
			}
				
		} else if(positions[3] > client.getScreenHight()-MIN_NOTIFICATION && cloume < MAX_LENGTH - 1){
			int clientIndex = mPositionMap[cloume+1][row];
			if(clientIndex == -1){
				return ;
			}else{
				verticalClient = mServerService.getClientByIndex(clientIndex);
				if(null != verticalClient && !isChanged(new int[]{cloume+1,row})){
					System.out.println("yadong changed -- "+(cloume+1)+" || "+row);
					int screenHeight = client.getScreenHight();
					bottom = positions[3]-screenHeight;
					top = 0 - (screenHeight - positions[1]);
				}
			}
		}
		if(left != -1 && right !=-1){
			horizontalClient.sendMessage(Client.CLIENT_CHANGE_POSITION+left+"|"+positions[1]+"|"+right+"|"+positions[3]);
			markChanged(getPositionOnMap(horizontalClient.getmIndex()));
			changePosition(horizontalClient, new int []{left,positions[1],right,positions[3]},false);
		}
		if(top!= -1 && bottom != -1){
			verticalClient.sendMessage(Client.CLIENT_CHANGE_POSITION+positions[0]+"|"+top+"|"+positions[2]+"|"+bottom);
			markChanged(getPositionOnMap(verticalClient.getmIndex()));
			changePosition(verticalClient, new int []{positions[0],top,positions[1],bottom},false);
		}
		//init changed
		mChanged = new boolean [MAX_LENGTH][MAX_LENGTH];
	}
	private void markChanged(int [] positions){
		mChanged[positions[0]][positions[1]] = true;
	}
	private boolean isChanged(int [] positions){
		return mChanged[positions[0]][positions[1]];
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
