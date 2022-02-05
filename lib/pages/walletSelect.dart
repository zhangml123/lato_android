import 'dart:ui';
import 'package:flutter/material.dart';

import 'package:flutter/services.dart';
import 'package:platon_fans/generated/l10n.dart';
import 'package:platon_fans/pages/backup.dart';

import 'package:platon_fans/pages/createWallet.dart';
import 'dart:convert' as convert;
class WalletSelectView extends StatefulWidget {
  @override
  WalletSelectViewState createState() => WalletSelectViewState();
}

class WalletSelectViewState extends State<WalletSelectView> {
  PageController pageController = PageController(viewportFraction: .8);
  var paddingLeft = 0.0;

  ///
  ///
  /// Bool to set true or false color button
  ///
  ///
  late int _position;
  late List _nets = [];
  late List _walletList = [];
  late int _nodeId ;
  bool checked = false;
  Color backgroundColor = Color(0xFFeeeeee).withOpacity(1);
  late BuildContext dialogContext;
  static const messageChannel = const BasicMessageChannel(
      'BasicMessageChannelPlugin', StandardMessageCodec());
  void initState() {
    super.initState();
    //receiveMessage();
    sendMessage();
  }
  Future<void> sendMessage() async {
    Map msg = new Map();
    msg["method"] = "get_node_list";
    String reply = (await messageChannel.send(msg)) as String;
    Map<String, dynamic> rs = convert.jsonDecode(reply);
    print("reply flutter receive3333  = "+ reply);
    var method =  rs["method"];
    if(method == "get_node_list"){
      List<dynamic> nodeList = rs["node_list"];
      late int nodeId ;

      for (var i = 0; i <nodeList.length; i++) {
        if(nodeList[i]["checked"]){
          nodeId = nodeList[i]["id"];
          _position = i ;
        }
      }
       this.setState(() {
         _nets = rs["node_list"];
         _nodeId = nodeId;
       });
       getWalletList();
    }
  }
  Future<void> getWalletList() async {
    Map msg = new Map();
    msg["method"] = "get_wallet_list";
    msg["nodeId"] = _nodeId;
    print(_nodeId);
    String reply = (await messageChannel.send(msg)) as String;
    Map<String, dynamic> rs = convert.jsonDecode(reply);
    print("reply flutter receive4444  = "+ reply);
    var method =  rs["method"];
    if(method == "get_wallet_list"){
      this.setState(() {
        _walletList = rs["wallet_list"];
      });
    }

  }
  Future<void> switchWallet(String uuid) async {
    Map msg = new Map();
    msg["method"] = "switch_wallet";
    msg["uuid"] = uuid;
    msg["nodeId"] = _nodeId;
    print("uuid = "+ uuid);
    print("nodeId = ");
    print(_nodeId);
    String reply = (await messageChannel.send(msg)) as String;
    Map<String, dynamic> rs = convert.jsonDecode(reply);
    print("reply flutter receive4444  = "+ reply);
    var method =  rs["method"];
    if(method == "switch_wallet"){
      getWalletList();
      Future.delayed(Duration(seconds: 3),(){
        Navigator.pop(dialogContext);
        Navigator.pop(context,"refresh");
      });
    }
  }
  @override
  Widget build(BuildContext context) {
    print("_nets = ");
    print(_nets.length);
    return Scaffold(
      appBar: AppBar(
        elevation: 0.0,
        title: Text(
          S.of(context).walletSelect ,
          style: TextStyle(fontFamily: "Sofia", fontWeight: FontWeight.w600),
        ),
        backgroundColor: Colors.blue,
        centerTitle: true,
      ),
      body: Container(
        width: MediaQuery
            .of(context)
            .size
            .width,
        height: MediaQuery
            .of(context)
            .size
            .height,
        color: backgroundColor,
        child: Stack(
          children: <Widget>[
            Padding(
                padding: EdgeInsets.only(left: 80, top: 20),
                child: selectView()
            ),
            Container(
              color: Colors.white,
              height: MediaQuery
                  .of(context)
                  .size
                  .height,
              width: 60,
              padding: const EdgeInsets.only( top: 25),
            ),
              Container(
                width: 80,
                height: 200,

                decoration: BoxDecoration(
                  color: backgroundColor,
                  //1111111111111111111111111
                ),
                child:
              ListView.builder(
                itemCount: _nets.length,
                itemBuilder: (BuildContext context, int position) {
                  Map<String, dynamic> _net =  _nets[position];
                  Color color = Colors.white;
                  if(_position == position ){
                    color = backgroundColor;
                  }
                  return _position == position ?
                    InkWell(
                        onTap: () {
                          this.setState(() {
                            _position = position;
                            _nodeId = _net["id"];
                          });
                          getWalletList();
                        },
                        child:
                        Column(
                          mainAxisAlignment:MainAxisAlignment.start,
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Container(
                              height: 30,
                              width: 60,
                              decoration: BoxDecoration(
                                color: Colors.white,
                                borderRadius: BorderRadius.only(bottomRight : Radius.circular(10.0)),
                              ),
                            ),
                            Container(
                              decoration: BoxDecoration(
                                color: Colors.white,
                                borderRadius: BorderRadius.only(bottomRight : Radius.circular(10.0)),

                              ),
                              child:
                            Container(
                                width: 80,
                                margin: const EdgeInsets.only( bottom: 0,top:0,left:20),
                                padding:const EdgeInsets.only( bottom: 10,top:10),
                                decoration: BoxDecoration(
                                  borderRadius: BorderRadius.only(topLeft : Radius.circular(40.0),bottomLeft: Radius.circular(40.0)),
                                  color: backgroundColor,
                                ),
                                child:
                                    Container(
                                      width:40,
                                      height:40,
                                      //margin: const EdgeInsets.only( left:20),
                                      child:
                                      Image(
                                        width:40,
                                        height:40,
                                        image: position == 0 ? AssetImage("assets/images/main.png") : AssetImage("assets/images/test.png"),
                                      ),
                                    )

                            ),),

                            Container(
                              height: 10,
                              width: 60,
                              decoration: BoxDecoration(
                                color: Colors.white,
                                borderRadius: BorderRadius.only(topRight : Radius.circular(10.0)),

                              ),
                            ),
                          ],
                        )
                    ) : InkWell(
                      onTap: () {
                    this.setState(() {
                      _position = position;
                      _nodeId = _net["id"];
                    });
                    getWalletList();
                  },
                  child:
                  Align(
                  alignment: Alignment.topLeft,
                    child:Container(

                      width:60,
                      height:100,
                      decoration: BoxDecoration(
                        //borderRadius: BorderRadius.only(topLeft : Radius.circular(40.0),bottomLeft: Radius.circular(40.0)),
                        color: Colors.white,

                      ),
                      child:
                      Align(
                        alignment: Alignment.center,
                      child:Image(

                        width:40,
                        height:40,
                        image: position == 0 ? AssetImage("assets/images/main.png") : AssetImage("assets/images/test.png"),
                      ),)

                  ))
                  );


                },

              ),
                )



            ///
            ///
            /// Create left bottom nav bar
            ///
            ///

          ],
        ),
      ),
    );
  }
  Widget selectView() {
    return Container(
      child: Column(
        children: [
          Container(
            alignment: Alignment.topLeft,
            child:
              Text(
                _nets[_position]["nodeName"],
                style: TextStyle(
                    fontSize: 18,
                    color: Colors.black54,
                    fontWeight: FontWeight.w700,
                    fontFamily: "Sofia"),
              )
            ,
          ),
          Padding(
          padding: const EdgeInsets.only(left: 0, right: 15.0, top: 15.0),
          child:
          InkWell(
            onTap: () {
              Navigator.push(
                context,
                MaterialPageRoute(builder: (context) {
                  return CreateWalletView(_nodeId);
                  //return BackupView();
                }),
              );
            },
            child:
            Container(
                height: 50.0,
                width: MediaQuery.of(context).size.width,
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.all(Radius.circular(15.0)),
                  color: Colors.white,
                  boxShadow: [
                    BoxShadow(
                      color: Color(0xFFABABAB).withOpacity(0.4),
                      offset : Offset(3.0, 3.0),
                      blurRadius: 5.0,
                      spreadRadius: 5.0,
                    ),
                  ],
                ),

                child:
                    Row(
                      crossAxisAlignment: CrossAxisAlignment.center,
                      mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Icon(
                          Icons.add_box_outlined,
                          size: 16,
                          color: Colors.blue
                      ),
                      Text(S.of(context).addWallet,
                        style: TextStyle(
                            fontSize: 16,
                            color: Colors.blue,
                            fontWeight: FontWeight.w400,
                            fontFamily: "Sofia"),)
                    ],
                  ),


            ),
          ),
          ),
          Container(
            height: MediaQuery.of(context).size.height-200,
            width: MediaQuery.of(context).size.width,
            padding: const EdgeInsets.only( top: 25,right: 15),
            child: ListView.builder(
              itemCount: _walletList.length,
              itemBuilder: (BuildContext context, int index) {

                print("_walletList 22222 = ");
                print(_walletList[index]["uuid"]);
                print(_walletList[index]["selected"]);
                return
                  InkWell(
                      onTap: () {
                        if(!_walletList[index]["selected"] && !checked){
                          checked = true;
                          showDialog(
                              context: context,
                              barrierDismissible: false,
                              builder: (BuildContext context) {
                                dialogContext = context;
                                return  Container(
                                  alignment: Alignment.center,
                                  child:Container(
                                    width: 150,
                                    height: 100,
                                    alignment: Alignment.center,
                                    decoration: BoxDecoration(
                                      borderRadius: BorderRadius.all(Radius.circular(5.0)),
                                      color: Colors.white,
                                      boxShadow: [
                                        BoxShadow(
                                          color: Color(0xFFABABAB).withOpacity(0.4),
                                          offset : Offset(3.0, 3.0),
                                          blurRadius: 5.0,
                                          spreadRadius: 5.0,
                                        ),
                                      ],
                                    ),
                                    child:
                                    Column(
                                      crossAxisAlignment: CrossAxisAlignment.center,
                                      mainAxisAlignment: MainAxisAlignment.center,
                                      children: [
                                        Container(
                                          width:30,
                                            height: 30,
                                            child: CircularProgressIndicator(
                                          valueColor:
                                          AlwaysStoppedAnimation<Color>(Colors.blue),
                                          backgroundColor: Colors.white,
                                          strokeWidth: 3,
                                        ),),
                                      SizedBox(height: 10,),
                                      Text("切换钱包...",
                                        style: TextStyle(
                                            fontSize: 18,
                                            color: Colors.grey,
                                            fontWeight: FontWeight.w700,
                                            fontFamily: "Sofia"),),
                                    ],)
                                  )
                                );
                              }
                          );
                          switchWallet(_walletList[index]["uuid"]);
                        }
                      },
                      child:
                        Container(
                        margin:const EdgeInsets.only( bottom: 10),
                          height:60,
                        decoration: BoxDecoration(
                          borderRadius: BorderRadius.all(Radius.circular(15.0)),
                          color: Colors.white,
                          boxShadow: [
                            BoxShadow(
                              color: Color(0xFFAFAFAF).withOpacity(0.4),
                              offset : Offset(3.0, 3.0),
                              blurRadius: 5.0,
                              spreadRadius: 5.0,
                            ),
                          ],
                        ),
                        child:
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            SizedBox(width: 1,),
                            Image(
                                image:  AssetImage("assets/images/main.png") ,
                                width: 40,
                                height: 40,
                                fit:BoxFit.fill,
                            ),
                            Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                mainAxisAlignment: MainAxisAlignment.center,
                                children: [
                                  Text( _walletList[index]["name"],
                                    softWrap:false ,
                                    overflow: TextOverflow.ellipsis,
                                    style: TextStyle(

                                        fontSize: 16,
                                        color: Colors.black87,
                                        fontWeight: FontWeight.w400,
                                        fontFamily: "Sofia"),
                                  ),
                                  Container(
                                      width:MediaQuery.of(context).size.width-200,
                                      child:
                                  Text( _walletList[index]["address"],
                                    softWrap:false ,
                                    overflow: TextOverflow.ellipsis,
                                    style: TextStyle(
                                        fontSize: 14,
                                        color: Colors.black45,
                                        fontWeight: FontWeight.w400,
                                        fontFamily: "Sofia"),
                                  )),

                                ]
                            ),
                            Icon(
                                  _walletList[index]["selected"] ?  Icons.check_circle : null,
                                  size: 20,
                                  color: Colors.blue
                            ),
                            SizedBox(width: 1,),
                          ],
                        )

                      )   );
              },


            )


          )

        ],
      ),
    );

  }
}