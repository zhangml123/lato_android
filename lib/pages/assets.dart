import 'dart:async';
import 'package:flutter/material.dart';
import 'package:platon_fans/pages/asset/list.dart';
import 'dart:convert' as convert;
import 'package:flutter/services.dart';
import 'package:platon_fans/pages/scan.dart';
import 'package:platon_fans/pages/walletSelect.dart';
import 'asset/receive.dart';
import 'asset/send.dart';
class AssetView extends StatefulWidget {
  @override
  AssetViewState createState() {
    return new AssetViewState();
  }
}

class AssetViewState extends State<AssetView>{
  String _address = "";
  String _name = "";
  String _currentNode="";
  String _balance = "0";
  double progress = 0.0;
  bool update = false;
  List _assetList = [];
  bool _newMessage = false ;
  static const messageChannel = const BasicMessageChannel(
      'BasicMessageChannelPlugin', StandardMessageCodec());
  void initState() {
    super.initState();
    receiveMessage();
    sendMessage();
  }
  Future<void> refresh() async{
    print("asset sendMessage refresh = ");
    receiveMessage();
    sendMessage();
  }
  Future<void> sendMessage() async {
    Map msg = new Map();
    msg["method"] = "get_wallet_address";
    String reply = (await messageChannel.send(msg)) as String;
    print("asset sendMessage get_wallet_address reply = "+ reply);
    Map<String, dynamic> rs = convert.jsonDecode(reply);
    var method =  rs["method"];
    if(method == "get_wallet_address"){
      var address =  rs["address"];
      var name =  rs["name"];
      this.setState(() {
        _address = address;
        _name = name;
      });
    }
    Map msg1 = new Map();
    msg1["method"] = "get_current_node";
    String reply1 = (await messageChannel.send(msg1)) as String;
    print("asset sendMessage get_current_node reply = "+ reply1);
    Map<String, dynamic> rs1 = convert.jsonDecode(reply1);
    var method1 =  rs1["method"];
    if(method1 == "get_current_node"){
      var nodeName = rs1["nodeName"];
      this.setState(() {
        print("reply flutter receive111111  = "+ reply1);
        _currentNode = nodeName;
      });
    }

    Map msg2 = new Map();
    msg2["method"] = "get_asset_list";
    String reply2 = (await messageChannel.send(msg2)) as String;
    print("asset sendMessage get_asset_list reply = "+ reply2);
    Map<String, dynamic> rs2 = convert.jsonDecode(reply2);
    var method2 =  rs2["method"];
    if(method2 == "get_asset_list"){
      this.setState(() {
        _assetList =  rs2["asset_list"];
      });
    }


    Map msg3 = new Map();
    msg3["method"] = "check_update";
    print("home sendMessage  = ");
    String reply3 = (await messageChannel.send(msg3)) as String;


    Map msg4 = new Map();
    msg4["method"] = "new_message";
    String reply4 = (await messageChannel.send(msg4)) as String;
    print("reply flutter receive44  = "+ reply4);

    Map<String, dynamic> rs4 = convert.jsonDecode(reply4);
    var method4 =  rs4["method"];
    if(method4 == "new_message"){
      var code = rs4["code"];
      if(code =="1"){
        this.setState(() {
          _newMessage = true;
        });
      }else{
        this.setState(() {
          _newMessage = false;
        });
      }
    }

  }
  void receiveMessage(){
    messageChannel.setMessageHandler((result) async {
      print("asset receiveMessage = "+ result.toString());
      //解析 原生发给 Flutter 的参数
      Map<String, dynamic> rs = convert.jsonDecode(result.toString());
      var method =  rs["method"];
      if(method == "get_wallet_address"){
        var address =  rs["address"];
        this.setState(() {
          _address = address;
        });
      }

      if(method == "get_asset_list"){
        this.setState(() {
          _assetList =  rs["asset_list"];
        });
      }

      if(method == "new_message"){
        var code = rs["code"];
        if(code =="1"){
          this.setState(() {
            _newMessage = true;
          });
        }else{
          this.setState(() {
            _newMessage = false;
          });
        }
      }

      if(method == "check_update"){
        if(rs["update"] == "true"){
          print("update notice= ");
          print(rs["json"]["versionCode"]);
          showUpdateNotification();
        }

      }
      return 'Flutter 已收到消息';
    });
  }


  Future<void> getProgress(  context1, Function state) async {

    messageChannel.setMessageHandler((result) async {


      Map<String, dynamic> rs = convert.jsonDecode(result.toString());

      var method =  rs["method"];
      if(method == "update_progress"){
        double _progress = rs["progress"];
        print("progress1111=");
        print( _progress);
        print("progress1111=");
        print( context);
        state(() {
          progress = _progress;
        });
        if(_progress==1.0){
          Future.delayed(Duration(seconds: 3),(){
            state(() {
              update = false;
            });
          });

        }
      }

      return 'Flutter 已收到消息';
    });
  }

  showUpdateNotification(){

    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (BuildContext context1) {
        return StatefulBuilder(builder: (context, state){
          print("progress1111222222=");
          print( progress);
        return Dialog(

          child:
              Container(
              height: 320,
              child:
              Column(
                children: [
                  Container(
                    height:130 ,
                    width: MediaQuery.of(context).size.width ,
                    decoration: BoxDecoration(
                      color:  Color(0xFFFFFFFF).withOpacity(0),

                      image:
                      DecorationImage(image: AssetImage("assets/images/update.png"), fit: BoxFit.fill),

                    ),
                  ),

                  Container(
                    padding:const EdgeInsets.only(left: 20.0, right: 20.0, top: 20.0, bottom: 20.0),
                    height: 180,
                    child: !update ? Column(
                      children: [
                        Text("更新提示",
                            style: TextStyle(
                                fontFamily: "Sofia",
                                fontWeight: FontWeight.w400,
                                color: Colors.black54,
                                fontSize: 18.0)),
                        SizedBox(height: 5,),
                        Text("有新版本是否立刻更新？",
                            style: TextStyle(
                                fontFamily: "Sofia",
                                color: Color(0xFF444444).withOpacity(1),
                                fontSize: 14.0)),
                        SizedBox(height: 20,),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            ElevatedButton(
                              style: ButtonStyle(
                                textStyle: MaterialStateProperty.all(
                                    TextStyle(fontSize: 18,)),
                                padding: MaterialStateProperty.all(EdgeInsets.only(left:35,right: 35,top:5,bottom: 5)),
                                shape: MaterialStateProperty.all(
                                    StadiumBorder(
                                        side: BorderSide(
                                          color: Colors.white,
                                        )
                                    )
                                )
                                ,
                                backgroundColor:MaterialStateProperty.all(Color(0xFFFFFFFF)),

                              ),
                              child: Text(
                                "取消",
                                style: TextStyle(color: Colors.blue),
                              ),

                              onPressed: () {
                                (() async {
                                  Map msg = new Map();
                                  msg["method"] = "cancel_update";
                                  String reply1 = (await messageChannel.send(msg)) as String;
                                })();

                                Navigator.pop(context);
                              },
                            ),
                            ElevatedButton(
                              style: ButtonStyle(
                                textStyle: MaterialStateProperty.all(
                                    TextStyle(fontSize: 18,)),
                                padding: MaterialStateProperty.all(EdgeInsets.only(left:35,right: 35,top:5,bottom: 5)),
                                shape: MaterialStateProperty.all(
                                    StadiumBorder(
                                        side: BorderSide(

                                          color: Colors.white,
                                        )
                                    )
                                )
                                ,
                                backgroundColor:MaterialStateProperty.all(Color(0xFFFFFFFF)),

                              ),
                              child: Text(
                                "确定",
                                style: TextStyle(color: Colors.blue),
                              ),

                              onPressed: () {
                                (() async {
                                  Map msg = new Map();
                                  msg["method"] = "update_app";
                                   String reply1 = (await messageChannel.send(msg)) as String;
                                })();
                                state(() {
                                  update = true;
                                });
                                getProgress(context,state);


                                 //Navigator.pop(context);
                              },
                            ),


                          ],
                        ),

                      ],
                    ): Column(
                        children: [
                          Text("下载中...",
                              style: TextStyle(
                                  fontFamily: "Sofia",
                                  fontWeight: FontWeight.w400,
                                  color: Colors.black54,
                                  fontSize: 18.0)),
                          SizedBox(height: 50,),
                          Container(
                            child: SizedBox(
                              width: 200,
                              height: 10,
                              child: new LinearProgressIndicator(
                                value: progress,
                                valueColor:
                                AlwaysStoppedAnimation<Color>(Colors.blue),
                                backgroundColor: Colors.black12,
                              ),
                            ),
                          ),
                        ]),
                  ),
                ],
              )
              )

        );
        });
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return new Scaffold(

        resizeToAvoidBottomInset: false,
        body: LayoutBuilder(
            builder: (context, constrains){
              return ListView(
                  children: <Widget>[
                    Container(
                decoration: BoxDecoration(
                  image:  DecorationImage(image: AssetImage("assets/images/background.png"), fit: BoxFit.fill),
                ),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.start,
                  children: [
                      Container(
                        height: 60.0,
                        width: MediaQuery.of(context).size.width,
                        child:
                        Padding(
                          padding: const EdgeInsets.only(left: 10.0, right: 10.0, top: 25),
                          child:
                          Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: <Widget>[
                                Container(
                                  height: 25.0,
                                  width: 80.0,
                                  margin:const EdgeInsets.only(left: 10.0, ),
                                  decoration: BoxDecoration(
                                    //borderRadius: BorderRadius.all(Radius.circular(15.0)),
                                    image:
                                    DecorationImage(image: AssetImage("assets/images/logo1.png"), fit: BoxFit.fill),

                                  ),
                                ),
                                Container(
                                  height: 70.0,
                                  width: 40.0,
                                  child: Center(
                                    child:
                                    IconButton(
                                      icon: const Icon(Icons.format_list_bulleted,color: Colors.white,),

                                      onPressed: () {
                                        Navigator.push(
                                          context,
                                          MaterialPageRoute(builder: (context) {
                                            return WalletSelectView();
                                          }),
                                        ).then((data){
                                          if(data == "refresh"){
                                            receiveMessage();
                                            sendMessage();
                                          }
                                        });
                                      },
                                    ),
                                  ),
                                ),
                              ]),
                        )
                      ),
                    Padding(
                      padding:
                      const EdgeInsets.only(left: 10.0, right: 10.0, top: 8.0, bottom: 5.0),
                      child: Container(
                        height: 130.0,
                        width: 400.0,

                        child: DecoratedBox(
                            decoration: BoxDecoration(
                              color: Colors.white,
                              borderRadius: BorderRadius.all(Radius.circular(15.0)),
                              boxShadow: [
                                BoxShadow(
                                  color: Color(0xFFABABAB).withOpacity(0.1),
                                  blurRadius: 3.0,
                                  spreadRadius: 3.0,
                                ),
                              ],
                            ),
                            child: Container(
                              decoration: BoxDecoration(
                                borderRadius: BorderRadius.all(Radius.circular(15.0)),
                                color: Colors.white,
                              ),
                              child: Padding(
                                padding:
                                const EdgeInsets.only(left: 20.0, right: 20.0, top: 20.0, bottom: 20.0),
                                child: Column(
                                  children: <Widget>[
                                    Row(
                                      children: <Widget>[
                                        Container(
                                          height: 40.0,
                                          width: 40.0,
                                          decoration: BoxDecoration(
                                            color: Colors.white,
                                            //borderRadius: BorderRadius.all(Radius.circular(15.0)),
                                            image:
                                            DecorationImage(image: AssetImage("assets/images/main.png"), fit: BoxFit.fill),

                                          ),
                                        ),
                                        SizedBox(
                                          width: 10.0,
                                        ),
                                        Column(
                                            children: <Widget>[
                                              Container(
                                                height: 30.0,
                                                width: 220.0,
                                                child: Text(
                                                  _name,
                                                  style:
                                                  TextStyle(fontFamily: "Sofia", color: Colors.black87, fontSize: 18.0),
                                                ),
                                              ),
                                              Container(
                                                height: 30.0,
                                                width: 220.0,

                                                child: Text(
                                                  _currentNode,
                                                  style:
                                                  TextStyle(fontFamily: "Sofia", color: Colors.black54, fontSize: 18.0),
                                                ),

                                              ),
                                            ]),

                                      ],
                                    ),
                                    SizedBox(
                                      height: 10.0,
                                    ),
                                      Row(
                                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                        children: <Widget>[
                                            Row(
                                                mainAxisAlignment: MainAxisAlignment.start,
                                                children: <Widget>[
                                                  InkWell(
                                                      onTap: () {
                                                        // Navigator.of(context).push(PageRouteBuilder(
                                                        //    pageBuilder: (_, __, ___) => new AssetDetailView()));
                                                        Navigator.push(
                                                          context,
                                                          MaterialPageRoute(builder: (context) {
                                                            return ReceiveView(_address, _name);
                                                          }),
                                                        );
                                                      },
                                                    child: const Icon(
                                                        IconData(0xe6cb, fontFamily: 'MyIcons'),
                                                        size: 20,
                                                        color: Colors.deepPurpleAccent
                                                    ),
                                                  ),

                                                  SizedBox(
                                                    width: 5.0,
                                                  ),
                                                  Container(
                                                    width:MediaQuery.of(context).size.width-120,
                                                    child: Text(_address,
                                                      softWrap:false ,
                                                      overflow: TextOverflow.ellipsis,
                                                      style: TextStyle(
                                                        fontSize: 16
                                                      ),
                                                    ),
                                                  ),

                                                ]
                                            ),
                                          InkWell(
                                              onTap: () {
                                                // Navigator.of(context).push(PageRouteBuilder(
                                                //    pageBuilder: (_, __, ___) => new AssetDetailView()));
                                                Navigator.push(
                                                  context,
                                                  MaterialPageRoute(builder: (context) {
                                                    return Scan();
                                                  }),
                                                ).then((data){
                                                  print("data22222222222222= "+ data);
                                                  Navigator.push(
                                                    context,
                                                    MaterialPageRoute(builder: (context) {
                                                      return SendView(data, _assetList[0]["id"], _assetList[0]["assetType"], _assetList[0]["symbol"],);
                                                    }),
                                                  );
                                                });
                                              },
                                              child:
                                            const Icon(
                                                IconData(0xe682, fontFamily: 'MyIcons'),
                                                size: 20,
                                                color: Colors.deepPurpleAccent
                                            )),
                                        ]),



                                  ],
                                ),

                              ),

                            ),
                          ),

                      ),
                    ),
                    SizedBox(height: 10.0),

                    Container(
                      height: constrains.maxHeight - 250,
                      decoration: BoxDecoration(
                        borderRadius: BorderRadius.all(Radius.circular(15.0)),
                        color: Colors.blue,
                      ),
                      child: new AssetListView(_balance, _assetList, _newMessage, refresh),
                    ),
                    /*Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: [
                  Container(
                    height: 40.0,
                    width: 150.0,
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.all(Radius.circular(15.0)),
                      color: Colors.blue,
                    ),
                    child: Center(
                      child: Text(
                        "收款",
                        style:
                        TextStyle(fontFamily: "Sofia", color: Colors.white, fontSize: 18.0),
                      ),
                    ),
                  ),
                  Container(
                    height: 40.0,
                    width: 150.0,
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.all(Radius.circular(15.0)),
                      color: Colors.blue,
                    ),
                    child: Center(
                      child: Text(
                        "转账",
                        style:
                        TextStyle(fontFamily: "Sofia", color: Colors.white, fontSize: 18.0),
                      ),
                    ),
                  ),
                ]
              ),*/
                  ],
                ),
              ),

                  ]);
            }
        )

    );
  }
}