import 'dart:ui';
import 'package:flutter/material.dart';

import 'package:flutter/services.dart';
import 'package:platon_fans/pages/backup.dart';

import 'package:platon_fans/pages/createWallet.dart';
import 'dart:convert' as convert;

import 'package:platon_fans/pages/walletManager.dart';
class WalletListView extends StatefulWidget {
  @override
  WalletListViewState createState() => WalletListViewState();
}

class WalletListViewState extends State<WalletListView> {
  PageController pageController = PageController(viewportFraction: .8);
  var paddingLeft = 0.0;

  ///
  ///
  /// Bool to set true or false color button
  ///
  ///
  late List _nets = [];
  late List _walletList = [];
  late int _nodeId;

  String _nodeName = "";
  bool checked = false;
  Color backgroundColor = Color(0xFFEDEDED).withOpacity(1);
  late BuildContext dialogContext;
  static const messageChannel = const BasicMessageChannel(
      'BasicMessageChannelPlugin', StandardMessageCodec());

  void initState() {
    super.initState();
    //receiveMessage();
    sendMessage();
  }

  Future<void> sendMessage() async {
    Map msg1 = new Map();
    msg1["method"] = "get_current_node";
    String reply1 = (await messageChannel.send(msg1)) as String;
    Map<String, dynamic> rs1 = convert.jsonDecode(reply1);
    print("reply flutter receive33332222  = " + reply1);
    var method1 = rs1["method"];
    if (method1 == "get_current_node") {
      this.setState(() {
        _nodeId = rs1["nodeId"];
        _nodeName = rs1["nodeName"];
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
    print("reply flutter receive4444  = " + reply);
    var method = rs["method"];
    if (method == "get_wallet_list") {
      this.setState(() {
        _walletList = rs["wallet_list"];
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        elevation: 0.0,
        title: Text(
          "钱包列表",
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
                padding: EdgeInsets.only(left: 10, top: 20),
                child: selectView()
            ),


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
                  _nodeName,
                  style: TextStyle(
                      fontSize: 18,
                      color: Colors.black54,
                      fontWeight: FontWeight.w700,
                      fontFamily: "Sofia"),
                )
                ,
              ),

              Container(
                  height: MediaQuery
                      .of(context)
                      .size
                      .height - 200,
                  width: MediaQuery
                      .of(context)
                      .size
                      .width,
                  padding: const EdgeInsets.only(top: 25, right: 15),
                  child: ListView.builder(
                      itemCount: _walletList.length,
                      itemBuilder: (BuildContext context, int index) {
                        print("_walletList 22222 = ");
                        print(_walletList[index]["uuid"]);
                        print(_walletList[index]["selected"]);
                        return
                          InkWell(
                              onTap: () {
                                Navigator.push(
                                  context,
                                  MaterialPageRoute(builder: (context) {
                                    return WalletManagerView(
                                        _walletList[index]["uuid"],
                                        _walletList[index]["name"]);
                                  }),
                                );
                              },
                              child:
                              Container(
                                  margin: const EdgeInsets.only(bottom: 10),
                                  height: 60,
                                  decoration: BoxDecoration(
                                    borderRadius: BorderRadius.all(
                                        Radius.circular(15.0)),
                                    color: Colors.white,
                                    boxShadow: [
                                      BoxShadow(
                                        color: Color(0xFFABABAB).withOpacity(
                                            0.4),
                                        offset: Offset(3.0, 3.0),
                                        blurRadius: 5.0,
                                        spreadRadius: 5.0,
                                      ),
                                    ],
                                  ),
                                  child:
                                  Row(
                                    mainAxisAlignment: MainAxisAlignment
                                        .spaceBetween,
                                    children: [
                                      SizedBox(width: 1,),
                                      Image(
                                        image: AssetImage(
                                            "assets/images/main.png"),
                                        width: 40,
                                        height: 40,
                                        fit: BoxFit.fill,
                                      ),
                                      Column(
                                          crossAxisAlignment: CrossAxisAlignment
                                              .start,
                                          mainAxisAlignment: MainAxisAlignment
                                              .center,
                                          children: [
                                            Text(_walletList[index]["name"],
                                              softWrap: false,
                                              overflow: TextOverflow.ellipsis,
                                              style: TextStyle(

                                                  fontSize: 16,
                                                  color: Colors.black87,
                                                  fontWeight: FontWeight.w400,
                                                  fontFamily: "Sofia"),
                                            ),
                                            Container(
                                                width: MediaQuery
                                                    .of(context)
                                                    .size
                                                    .width - 200,
                                                child:
                                                Text(
                                                  _walletList[index]["address"],
                                                  softWrap: false,
                                                  overflow: TextOverflow
                                                      .ellipsis,
                                                  style: TextStyle(
                                                      fontSize: 14,
                                                      color: Colors.black45,
                                                      fontWeight: FontWeight
                                                          .w400,
                                                      fontFamily: "Sofia"),
                                                )),
                                          ]
                                      ),
                                    ],
                                  )
                              )
                          );
                      }
                  )
              )
            ]
        )
    );
  }
}