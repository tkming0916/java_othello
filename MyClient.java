import java.net.*;
import java.io.*;
import javax.swing.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.awt.image.*;//画像処理に必要
import java.awt.geom.*;//画像処理に必要
import java.applet.*;	//wavファイルの再生に使用

public class MyClient extends JFrame implements MouseListener,MouseMotionListener { 
	private JButton buttonArray[][];//ボタン用の配列
    private JButton buttonchange, buttonreset;
	private Container c;
	private ImageIcon blackIcon, whiteIcon, boardIcon, canIcon;
    private ImageIcon myIcon, yourIcon;
    private int myColor;
    private int myTurn;
    
    private ImageIcon seticon1, seticon2;
    private AudioClip clip;
    private int bgmnum;
    
    
 
    PrintWriter out;//出力用のライター

	public MyClient() {
		//名前の入力ダイアログを開く
		String myName = JOptionPane.showInputDialog(null,"名前を入力してください","名前の入力",JOptionPane.QUESTION_MESSAGE);
		if(myName.equals("")){
			myName = "No name";//名前がないときは，"No name"とする
		}
        String IPName = JOptionPane.showInputDialog(null,"IPアドレスを入力してください","IPアドレスの入力",JOptionPane.QUESTION_MESSAGE);
        if(IPName.equals("")){
			IPName = "localhost";//名前がないときは，"localhost"とする
		}

		//ウィンドウを作成する
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//ウィンドウを閉じるときに，正しく閉じるように設定する
		setTitle("MyClient");//ウィンドウのタイトルを設定する
		setSize(600,600);//ウィンドウのサイズを設定する
		c = getContentPane();//フレームのペインを取得する

		//アイコンの設定
		String iconName = "./img/White.jpg";
		whiteIcon = new ImageIcon(iconName);
		blackIcon = new ImageIcon("Black.jpg");
		boardIcon = new ImageIcon("GreenFrame.jpg");
        canIcon = new ImageIcon("canset.jpg");

		c.setLayout(null);//自動レイアウトの設定を行わない
        
        
		//ボタンの生成
		buttonArray = new JButton[8][8];//ボタンの配列を５個作成する[0]から[4]まで使える
		for(int i=0;i<8;i++){
            for(int j=0; j<8;j++){
                buttonArray[j][i] = new JButton(boardIcon);//ボタンにアイコンを設定する
                c.add(buttonArray[j][i]);//ペインに貼り付ける
                buttonArray[j][i].setBounds(i*50+10,j*50+10,50,50);//ボタンの大きさと位置を設定する．(x座標，y座標,xの幅,yの幅）
                buttonArray[j][i].addMouseListener(this);//ボタンをマウスでさわったときに反応するようにする
                buttonArray[j][i].addMouseMotionListener(this);//ボタンをマウスで動かそうとしたときに反応するようにする
                buttonArray[j][i].setActionCommand(Integer.toString(j*8+i));//ボタンに配列の情報を付加する（ネットワークを介してオブジェクトを識別するため）
            }
        }
        
        buttonArray[3][3].setIcon(whiteIcon);
        buttonArray[3][4].setIcon(blackIcon);
        buttonArray[4][3].setIcon(blackIcon);
        buttonArray[4][4].setIcon(whiteIcon);
        
        
        
        //いつでもパスができるボタンの生成と配置
        buttonchange = new JButton("パス");
        c.add(buttonchange);
        buttonchange.setBounds(430,10,140,50);
        buttonchange.addMouseListener(this);
        
        //いつでもリセットできるボタンの生成と配置
        buttonreset = new JButton("リセット");
        c.add(buttonreset);
        buttonreset.setBounds(430,65,140,50);
        buttonreset.addMouseListener(this);
        
		
		//サーバに接続する
		Socket socket = null;
		try {
			//"localhost"は，自分内部への接続．localhostを接続先のIP Address（"133.42.155.201"形式）に設定すると他のPCのサーバと通信できる
			//10000はポート番号．IP Addressで接続するPCを決めて，ポート番号でそのPC上動作するプログラムを特定する
			socket = new Socket(IPName, 10000);
		} catch (UnknownHostException e) {
			System.err.println("ホストの IP アドレスが判定できません: " + e);
		} catch (IOException e) {
			 System.err.println("エラーが発生しました: " + e);
		}
		
		MesgRecvThread mrt = new MesgRecvThread(socket, myName);//受信用のスレッドを作成する
		mrt.start();//スレッドを動かす（Runが動く）
	}
		
	//メッセージ受信のためのスレッド
	public class MesgRecvThread extends Thread {
		Socket socket;
		String myName;
		
		public MesgRecvThread(Socket s, String n){
			socket = s;
			myName = n;
		}
		
		//通信状況を監視し，受信データによって動作する
		public void run() {
			try{
                
                int count = 0;
                
				InputStreamReader sisr = new InputStreamReader(socket.getInputStream());
				BufferedReader br = new BufferedReader(sisr);
				out = new PrintWriter(socket.getOutputStream(), true);
				out.println(myName);//接続の最初に名前を送る
                String myNumberStr = br.readLine();
                int myNumberInt = Integer.parseInt(myNumberStr);
                
                JLabel blackHPcount = new JLabel("0");
                JLabel whiteHPcount = new JLabel("0");
                
                if (myNumberInt % 2 == 0){
                    myColor = 0;//黒のコマ
                    System.out.println("黒");
                }else{
                    myColor = 1;//白のコマ
                    System.out.println("白");
                }
                
                if (myColor == 0){
                    myIcon = blackIcon;
                    yourIcon = whiteIcon;
                    myTurn = 0;
                    
                    seticon1 = new ImageIcon("Black.jpg");
                    JButton setblackIcon = new JButton(seticon1);//ボタンを作成，画像を設定する
                    c.add(setblackIcon);//ペインに貼り付ける
                    setblackIcon.setBounds(10,420,50,50);
					
                    seticon2 = new ImageIcon("White.jpg");
                    JButton whileIconset = new JButton(seticon2);//ボタンを作成，画像を設定する
                    c.add(whileIconset);//ペインに貼り付ける
                    whileIconset.setBounds(10,480,50,50);
                    
                    
                    buttonArray[2][3].setIcon(canIcon);
                    buttonArray[3][2].setIcon(canIcon);
                    buttonArray[4][5].setIcon(canIcon);
                    buttonArray[5][4].setIcon(canIcon);
                    
                    
                }else{
                    myIcon = whiteIcon;
                    yourIcon = blackIcon;
                    myTurn = 1;
                    
                    seticon1 = new ImageIcon("White.jpg");//なにか画像ファイルをダウンロードしておく
                    JButton setblackIcon = new JButton(seticon1);//ボタンを作成，画像を設定する
                    c.add(setblackIcon);//ペインに貼り付ける
                    setblackIcon.setBounds(10,420,50,50);
                    
                    seticon2 = new ImageIcon("Black.jpg");//なにか画像ファイルをダウンロードしておく
                    JButton whileIconset = new JButton(seticon2);//ボタンを作成，画像を設定する
                    c.add(whileIconset);//ペインに貼り付ける
                    whileIconset.setBounds(10,480,50,50);
                }
                
                
                //ラベルの生成
                JLabel theLabel1 = new JLabel("自分のコマ数 : ");
                c.add(theLabel1);
                theLabel1.setBounds(70,420,140,50);
                theLabel1.setForeground(Color.BLACK);
                
                JLabel theLabel2 = new JLabel("相手のコマ数 : ");
                c.add(theLabel2);
                theLabel2.setBounds(70,480,140,50);
                theLabel2.setForeground(Color.BLACK);
                
                JLabel theLabel3 = new JLabel("2");
                c.add(theLabel3);
                theLabel3.setBounds(175,420,50,50);
                theLabel3.setForeground(Color.BLACK);

                JLabel theLabel4 = new JLabel("2");
                c.add(theLabel4);
                theLabel4.setBounds(175,480,50,50);
                theLabel4.setForeground(Color.BLACK);
                
				while(true) {
                    
                    //ターンが切り替わるごとに変数の初期化を行う。
                    int blackIconNum = 0;
                    int whiteIconNum = 0;
                    int number0 = 0;
                    int countTurn = 0;
                    
                    
					String inputLine = br.readLine();//データを一行分だけ読み込んでみる
					if (inputLine != null) {//読み込んだときにデータが読み込まれたかどうかをチェックする
						//System.out.println(inputLine);//デバッグ（動作確認用）にコンソールに出力する
						String[] inputTokens = inputLine.split(" ");	//入力データを解析するために、スペースで切り分ける
						String cmd = inputTokens[0];//コマンドの取り出し．１つ目の要素を取り出す
						if(cmd.equals("MOVE")){//cmdの文字と"MOVE"が同じか調べる．同じ時にtrueとなる
							//MOVEの時の処理(コマの移動の処理)
                            //System.out.println("MOVE受信した！！！！");
							String theBName = inputTokens[1];//ボタンの名前（番号）の取得
							int theBnum = Integer.parseInt(theBName);//ボタンの名前を数値に変換する
							int x = Integer.parseInt(inputTokens[2]);//数値に変換する
							int y = Integer.parseInt(inputTokens[3]);//数値に変換する
							buttonArray[theBnum][theBnum].setLocation(x,y);//指定のボタンを位置をx,yに設定する
						}
                        
						if(cmd.equals("PLACE")){//cmdの文字と"PLACE"が同じか調べる．同じ時にtrueとなる
							//PLACEの時の処理(コマの色の処理)
                            System.out.println("PLACE受信した！！！！");
							String theBName = inputTokens[1];//ボタンの名前（番号）の取得
							int theBnum = Integer.parseInt(theBName);//ボタンの名前を数値に変換する
							int theColor = Integer.parseInt(inputTokens[2]);
							int x = theBnum % 8;//x座標
							int y = theBnum / 8;//y座標
                            
                            bgmnum = 0;

                            if (theColor == myColor){
                                //送信元クライアントでの処理
                                buttonArray[y][x].setIcon(myIcon);
                                SoundTestWav(bgmnum);
                                
                            }else{
                                //受信元クライアントでの処理
                                buttonArray[y][x].setIcon(yourIcon);
                                SoundTestWav(bgmnum);
                                
                            }
                            
                            if(countTurn == 0){
                                for(int i = 0; i < 8; i++){
                                    for(int j = 0; j < 8; j++){
                                        if(buttonArray[j][i].getIcon() == blackIcon){
                                            blackIconNum++;
                                        }else if(buttonArray[j][i].getIcon() == whiteIcon){
                                            whiteIconNum++;
                                        }else if((buttonArray[j][i].getIcon() == boardIcon) || (buttonArray[j][i].getIcon() == canIcon)){
                                            if(buttonArray[j][i].getIcon() == canIcon){
                                                buttonArray[j][i].setIcon(boardIcon);
                                            }
                                            if(judgeButton(j,i,number0,myTurn)){
                                                countTurn++;//次のターンに相手がコマを置ける状態
                                            }
                                        }
                                    }
                                }
                            }
                            count++;
                            
                            System.out.println(countTurn);
                            
                            if(countTurn != 0){
                                myTurn = 1 - myTurn;//ターンを入れ替える
                                count = 0;//スキップボタンの初期化
                            }
                            
                            String blackIconstr = Integer.toString(blackIconNum);
                            String whiteIconstr = Integer.toString(whiteIconNum);
							//コマの数を表示
                            if(myIcon == blackIcon){
                                theLabel3.setText(blackIconstr);
                                theLabel4.setText(whiteIconstr);
                            }else{
                                theLabel3.setText(whiteIconstr);
                                theLabel4.setText(blackIconstr);
                            }
                            
                            
                            if((count >= 2) || (blackIconNum + whiteIconNum == 64) || (blackIconNum * whiteIconNum == 0)){
                                judgeGame();
                            }
                            
                            
                            //次自分のターンの時、おける場所を表示する
                            if(myTurn == 0){//次自分のターンの時、おける場所を表示する
                                nextset();
                            }
                            
						}
                        
						if(cmd.equals("FLIP")){//cmdの文字と"FLIP"が同じか調べる．同じ時にtrueとなる
							//FLIPの時の処理(コマをひっくり返す処理)
                            System.out.println("FLIP受信した！！！！");
							String theBName = inputTokens[1];//ボタンの名前（番号）の取得
							int theBnum = Integer.parseInt(theBName);//ボタンの名前を数値に変換する
							int theColor = Integer.parseInt(inputTokens[2]);
							int x = theBnum % 8;//x座標
							int y = theBnum / 8;//y座標
                            
                            if(theColor == myColor){
                                buttonArray[y][x].setIcon(myIcon);
                            }else{
                                buttonArray[y][x].setIcon(yourIcon);
                            }
                            
						}
                        
                        
                        if(cmd.equals("CHANGETURN")){
                            bgmnum = 1;
                            SoundTestWav(bgmnum);
                            for(int j = 0; j < 8; j++){
                                for(int i = 0; i < 8; i++){
                                    if((buttonArray[j][i].getIcon() == canIcon)){
                                        buttonArray[j][i].setIcon(boardIcon);
                                    }
                                }
                            }
                            if(myTurn == 0){
                                myTurn = 1;
                            }else{
                                myTurn = 0;
                                nextset();
                            }
                            count++;
                            if(count == 2){
                                judgeGame();
                            }
                        }
                        
                        
                        if(cmd.equals("REPLAY")){
                            blackIconNum = 0;
                            whiteIconNum = 0;
                            number0 = 0;
                            countTurn = 0;
                            count = 0;
                            
                            theLabel3.setText("2");
                            theLabel4.setText("2");
                            whiteHPcount.setText("0");
                            blackHPcount.setText("0");
                            
                            for(int i=0;i<8;i++){
                                for(int j=0; j<8;j++){
                                    buttonArray[j][i].setIcon(boardIcon);
                                }
                            }
                            
                            buttonArray[3][3].setIcon(whiteIcon);
                            buttonArray[3][4].setIcon(blackIcon);
                            buttonArray[4][3].setIcon(blackIcon);
                            buttonArray[4][4].setIcon(whiteIcon);
                            
                            if(myTurn == 0){
                                if(myColor == 0){
                                    buttonArray[2][3].setIcon(canIcon);
                                    buttonArray[3][2].setIcon(canIcon);
                                    buttonArray[4][5].setIcon(canIcon);
                                    buttonArray[5][4].setIcon(canIcon);
                                }else{
                                    buttonArray[2][4].setIcon(canIcon);
                                    buttonArray[3][5].setIcon(canIcon);
                                    buttonArray[4][2].setIcon(canIcon);
                                    buttonArray[5][3].setIcon(canIcon);
                                }
                            }
                            
                        }
                        
                        
					}else{
						break;
					}
				
				}
				socket.close();
			} catch (IOException e) {
				System.err.println("エラーが発生しました: " + e);
			}
		}
	}
    
    
    
    
	public static void main(String[] args) {
		MyClient net = new MyClient();
		net.setVisible(true);
	}
    
    
	//次におけるコマの場所を表示
    public void nextset(){
        int num = 2;
        int turn = 0;
        for(int j = 1; j < 8; j++){
            for(int i = 1; i < 8; i++){
                judgeButton(j,i,num,turn);
            }
        }
    }
    
	//ターンを変える
    public void changeTurn(){
        System.out.println("CHANGETURN");
        String msg = "CHANGETURN";
        out.println(msg);//送信データをバッファに書き出す
        out.flush();//送信データをフラッシュ（ネットワーク上にはき出す）する
    }
    
    //コマが置けるかどうかの判定をする.numberで「配置できるかの確認」と「自動パス」と「次に配置できる場所の表示」を切り替えている
    public boolean judgeButton(int y, int x, int number, int myTurn){
        boolean flag = false;
        
        for(int j=-1;j<2;j++){
            for(int i=-1;i<2;i++){
                if(!((i == 0) && (j == 0))){
                
                    if((number == 1) || (number == 2)){
                        int flipNum = flipButtons(y,x,j,i);
                        if(flipNum >= 1){
                            flag = true;
                            for(int dy=j, dx=i, k=0; k<flipNum; k++, dy+=j, dx+=i){
                                //ボタンの位置情報を作る
                                int msgy = y + dy;
                                int msgx = x + dx;
                                int theArrayIndex = msgy*8 + msgx;
                                if(number == 1){
                                    //サーバに情報を送る
                                    String msg = "FLIP"+" "+theArrayIndex+" "+myColor;
                                    out.println(msg);
                                    out.flush();
                                }else if(number == 2){
                                    //次置ける場所に黒丸表示
                                    if(buttonArray[y][x].getIcon() == boardIcon){
                                        buttonArray[y][x].setIcon(canIcon);
                                        //System.out.println("次置ける座標は（x,y ） = ( " + x + " , " + y + " ）");
                                    }
                                }
                                
                            }
                        }
                    }else if(number == 0){//number0のとき
                        if(myTurn == 0){
                            int flipNum2 = flipButtons2(y,x,j,i);
                            if(flipNum2 >= 1){
                                flag = true;
                            }
                        }else{
                            int flipNum2 = flipButtons(y,x,j,i);
                            if(flipNum2 >= 1){
                                flag = true;
                            }
                        }
                    }
                }
            }
        }
        return flag;
    }
	
    //ひっくり返すコマの数をカウントする
    public int flipButtons(int y, int x, int j, int i){
        int flipNum = 0;
        for(int dy=j, dx=i; ; dy+=j, dx+=i){
            if((y+dy < 0) || (y+dy > 7) || (x+dx < 0) || (x+dx > 7)){//場外ならすぐflipNumを返す
                return flipNum = 0;
            }
            if((buttonArray[y+dy][x+dx].getIcon() == boardIcon) || (buttonArray[y+dy][x+dx].getIcon() == canIcon)){//boardIconがあればflipNumをかえす
                return flipNum = 0;
            }else if(buttonArray[y+dy][x+dx].getIcon() == myIcon){//myIconならflipNumを返す
                return flipNum;
            }else if(buttonArray[y+dy][x+dx].getIcon() == yourIcon){//yourIconならflipNumに1を加える
                flipNum++;
            }
        }
    }
    
	
	//自動パス
    public int flipButtons2(int y, int x, int j, int i){
        int flipNum = 0;
        for(int dy=j, dx=i; ; dy+=j, dx+=i){
            if((y+dy < 0) || (y+dy > 7) || (x+dx < 0) || (x+dx > 7)){
                return flipNum = 0;
            }
            if(buttonArray[y+dy][x+dx].getIcon() == boardIcon){
                return flipNum = 0;
            }else if(buttonArray[y+dy][x+dx].getIcon() == yourIcon){
                return flipNum;
            }else if(buttonArray[y+dy][x+dx].getIcon() == myIcon){
                flipNum++;
            }
        }
    }
	
	
	//終了判定
    public void judgeGame(){
        int boardIconNum = 0;
        int myIconNum = 0;
        int yourIconNum = 0;
        String message;
        for(int i=0;i<8;i++){
            for(int j=0; j<8;j++){
                if(buttonArray[j][i].getIcon() == boardIcon){
                    boardIconNum++;
                }else if(buttonArray[j][i].getIcon() == myIcon){
                    myIconNum++;
                }else if(buttonArray[j][i].getIcon() == yourIcon){
                    yourIconNum++;
                }
            }
        }
        if(myIconNum > yourIconNum){
            bgmnum = 3;
            SoundTestWav(bgmnum);
            message = "あなたの勝ち！";
            WinDialogWindow dlg = new WinDialogWindow(this);
            setVisible(true);
        }else if (myIconNum < yourIconNum){
            bgmnum = 4;
            SoundTestWav(bgmnum);
            message = "あなたの負け！";
            LoseDialogWindow dlg = new LoseDialogWindow(this);
            setVisible(true);
        }else{
            message = "ひ　き　わ　け";
            DrawDialogWindow dlg = new DrawDialogWindow(this);
            setVisible(true);
        }
        System.out.println(message);
    }
	
	
	//ゲームの初期化
    public void resetGame(){
        bgmnum = 2;
        String msg = "REPLAY";//サーバに情報を送る
        out.println(msg);//送信データをバッファに書き出す
        out.flush();//送信データをフラッシュ（ネットワーク上にはき出す）する
        
        SoundTestWav(bgmnum);
        
    }
	
	
	public void mouseClicked(MouseEvent e) {//ボタンをクリックしたときの処理
		int number = 1;
		
		if (myTurn == 0){
            System.out.println("クリック!");
            
            
            JButton theButton = (JButton)e.getComponent();//クリックしたオブジェクトを得る．型が違うのでキャストする
            String theArrayIndex = theButton.getActionCommand();//ボタンの配列の番号を取り出す
            Icon theIcon = theButton.getIcon();//theIconには，現在のボタンに設定されたアイコンが入る
            //System.out.println(theIcon);//デバッグ（確認用）に，クリックしたアイコンの名前を出力する
            
            
            //クリックしたアイコンがboardIconかcanIconのときmsgを送信できる
            if((theIcon == boardIcon) || (theIcon == canIcon)){
                int temp = Integer.parseInt(theArrayIndex);//theArrayIndexを変数tempに変換
                int x = temp % 8;
                int y = temp /8;
                if(judgeButton(y,x,number,myTurn)){
                    //コマを置ける
                    String msg = "PLACE"+" "+theArrayIndex+" "+myColor;//サーバに情報を送る
                    out.println(msg);//送信データをバッファに書き出す
                    out.flush();//送信データをフラッシュ（ネットワーク上にはき出す）する
                }else{
                    //コマを置けない
                    System.out.println("そこには配置できません!");
                }
            }
            
            if(theButton == buttonchange){
                changeTurn();
            }
            
            if(theButton == buttonreset){
                resetGame();
            }
        }
        repaint();//オブジェクトの再描画を行う
	}
    
	public void mouseEntered(MouseEvent e) {//マウスがオブジェクトに入ったときの処理
		//System.out.println("マウスが入った");
	}
	
	public void mouseExited(MouseEvent e) {//マウスがオブジェクトから出たときの処理
		//System.out.println("マウス脱出");
	}
	
	public void mousePressed(MouseEvent e) {//マウスでオブジェクトを押したときの処理（クリックとの違いに注意）
		//System.out.println("マウスを押した");
	}
	
	public void mouseReleased(MouseEvent e) {//マウスで押していたオブジェクトを離したときの処理
		//System.out.println("マウスを放した");
	}
	
	public void mouseDragged(MouseEvent e) {//マウスでオブジェクトとをドラッグしているときの処理
    /*
		//System.out.println("マウスをドラッグ");
		JButton theButton = (JButton)e.getComponent();//型が違うのでキャストする
		String theArrayIndex = theButton.getActionCommand();//ボタンの配列の番号を取り出す
        
        if (theArrayIndex.charAt(0) == '0'){
            Point theMLoc = e.getPoint();//発生元コンポーネントを基準とする相対座標
            //System.out.println(theMLoc);//デバッグ（確認用）に，取得したマウスの位置をコンソールに出力する
            Point theBtnLocation = theButton.getLocation();//クリックしたボタンを座標を取得する
            theBtnLocation.x += theMLoc.x-15;//ボタンの真ん中当たりにマウスカーソルがくるように補正する
            theBtnLocation.y += theMLoc.y-15;//ボタンの真ん中当たりにマウスカーソルがくるように補正する
            theButton.setLocation(theBtnLocation);//マウスの位置にあわせてオブジェクトを移動する
 
            //送信情報を作成する（受信時には，この送った順番にデータを取り出す．スペースがデータの区切りとなる）
            String msg = "MOVE"+" "+theArrayIndex+" "+theBtnLocation.x+" "+theBtnLocation.y;

            //サーバに情報を送る
            out.println(msg);//送信データをバッファに書き出す
            out.flush();//送信データをフラッシュ（ネットワーク上にはき出す）する

            repaint();//オブジェクトの再描画を行う
        }
    */
        
	}

	public void mouseMoved(MouseEvent e) {//マウスがオブジェクト上で移動したときの処理
    /*
		//System.out.println("マウス移動");
		int theMLocX = e.getX();//マウスのx座標を得る
		int theMLocY = e.getY();//マウスのy座標を得る
		//System.out.println(theMLocX+","+theMLocY);//コンソールに出力する
    */
	}
	
	
	//bgmの管理
    public void SoundTestWav(int bgmnum){
        if(bgmnum == 0){
            //音源の読み込み
            clip = Applet.newAudioClip(getClass().getResource("place.wav"));
            clip.play();
        }else if(bgmnum == 1){
            clip = Applet.newAudioClip(getClass().getResource("skip.wav"));
            clip.play();
        }else if(bgmnum == 2){
            clip = Applet.newAudioClip(getClass().getResource("reset.wav"));
            clip.play();
        }else if(bgmnum == 3){
            clip = Applet.newAudioClip(getClass().getResource("win.wav"));
            clip.play();
        }else if(bgmnum == 4){
            clip = Applet.newAudioClip(getClass().getResource("lose.wav"));
            clip.play();
        }
    }
    
}


//勝ちのダイアログを出す
class WinDialogWindow extends JDialog implements ActionListener{
    WinDialogWindow(JFrame owner) {
        super(owner);//呼び出しもととの親子関係の設定．これをコメントアウトすると別々のダイアログになる

		Container c = this.getContentPane();	//フレームのペインを取得する
        c.setLayout(null);		//自動レイアウトの設定を行わない

        JButton theButton = new JButton();//画像を貼り付けるラベル
        ImageIcon theImage = new ImageIcon("win.jpg");//なにか画像ファイルをダウンロードしておく
        theButton.setIcon(theImage);//ラベルを設定
        theButton.setBounds(0,0,526,234);//ボタンの大きさと位置を設定する．(x座標，y座標,xの幅,yの幅）
        theButton.addActionListener(this);//ボタンをクリックしたときにactionPerformedで受け取るため
        c.add(theButton);//ダイアログに貼り付ける（貼り付けないと表示されない

        setTitle("You Win!");//タイトルの設定
        setSize(526, 234);//大きさの設定
        setResizable(false);//拡大縮小禁止//trueにすると拡大縮小できるようになる
        setUndecorated(true); //タイトルを表示しない
        setModal(true);//上を閉じるまで下を触れなくする（falseにすると触れる）

        //ダイアログの大きさや表示場所を変更できる
        //親のダイアログの中心に表示したい場合は，親のウィンドウの中心座標を求めて，子のダイアログの大きさの半分ずらす
        setLocation(owner.getBounds().x+owner.getWidth()/2-this.getWidth()/2,owner.getBounds().y+owner.getHeight()/2-this.getHeight()/2);
        setVisible(true);
    }
    public void actionPerformed(ActionEvent e) {
        this.dispose();//Dialogを廃棄する
    }
}

//負けのダイアログを出す
class LoseDialogWindow extends JDialog implements ActionListener{
    LoseDialogWindow(JFrame owner) {
        super(owner);//呼び出しもととの親子関係の設定．これをコメントアウトすると別々のダイアログになる

		Container c = this.getContentPane();	//フレームのペインを取得する
        c.setLayout(null);		//自動レイアウトの設定を行わない

        JButton theButton = new JButton();//画像を貼り付けるラベル
        ImageIcon theImage = new ImageIcon("lose.jpg");//なにか画像ファイルをダウンロードしておく
        theButton.setIcon(theImage);//ラベルを設定
        theButton.setBounds(0,0,526,234);//ボタンの大きさと位置を設定する．(x座標，y座標,xの幅,yの幅）
        theButton.addActionListener(this);//ボタンをクリックしたときにactionPerformedで受け取るため
        c.add(theButton);//ダイアログに貼り付ける（貼り付けないと表示されない

        setTitle("You Lose!");//タイトルの設定
        setSize(526, 234);//大きさの設定
        setResizable(false);//拡大縮小禁止//trueにすると拡大縮小できるようになる
        setUndecorated(true); //タイトルを表示しない
        setModal(true);//上を閉じるまで下を触れなくする（falseにすると触れる）

        //ダイアログの大きさや表示場所を変更できる
        //親のダイアログの中心に表示したい場合は，親のウィンドウの中心座標を求めて，子のダイアログの大きさの半分ずらす
        setLocation(owner.getBounds().x+owner.getWidth()/2-this.getWidth()/2,owner.getBounds().y+owner.getHeight()/2-this.getHeight()/2);
        setVisible(true);
    }
    public void actionPerformed(ActionEvent e) {
        this.dispose();//Dialogを廃棄する
    }
}


//ひきわけのダイアログを出す
class DrawDialogWindow extends JDialog implements ActionListener{
    DrawDialogWindow(JFrame owner) {
        super(owner);//呼び出しもととの親子関係の設定．これをコメントアウトすると別々のダイアログになる

		Container c = this.getContentPane();	//フレームのペインを取得する
        c.setLayout(null);		//自動レイアウトの設定を行わない

        JButton theButton = new JButton();//画像を貼り付けるラベル
        ImageIcon theImage = new ImageIcon("draw.jpg");//なにか画像ファイルをダウンロードしておく
        theButton.setIcon(theImage);//ラベルを設定
        theButton.setBounds(0,0,526,234);//ボタンの大きさと位置を設定する．(x座標，y座標,xの幅,yの幅）
        theButton.addActionListener(this);//ボタンをクリックしたときにactionPerformedで受け取るため
        c.add(theButton);//ダイアログに貼り付ける（貼り付けないと表示されない

        setTitle("!!DRAW!!");//タイトルの設定
        setSize(526, 234);//大きさの設定
        setResizable(false);//拡大縮小禁止//trueにすると拡大縮小できるようになる
        setUndecorated(true); //タイトルを表示しない
        setModal(true);//上を閉じるまで下を触れなくする（falseにすると触れる）

        //ダイアログの大きさや表示場所を変更できる
        //親のダイアログの中心に表示したい場合は，親のウィンドウの中心座標を求めて，子のダイアログの大きさの半分ずらす
        setLocation(owner.getBounds().x+owner.getWidth()/2-this.getWidth()/2,owner.getBounds().y+owner.getHeight()/2-this.getHeight()/2);
        setVisible(true);
    }
    public void actionPerformed(ActionEvent e) {
        this.dispose();//Dialogを廃棄する
    }
}

