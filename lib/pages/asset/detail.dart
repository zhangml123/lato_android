import 'package:flutter/material.dart';
import 'package:platon_fans/generated/l10n.dart';
import 'package:platon_fans/pages/history/list.dart';
import 'package:platon_fans/pages/asset/receive.dart';
import 'package:platon_fans/pages/asset/send.dart';
import 'package:flutter/services.dart';
import 'dart:convert' as convert;
class AssetDetailView extends StatefulWidget {
  String id;
  String symbol;
  int assetType;
  AssetDetailView(this.id, this.symbol, this.assetType):super();

  @override
  AssetDetailViewState createState() {
    return new AssetDetailViewState();
  }
}

class AssetDetailViewState extends State<AssetDetailView>{
  String _address = "";
  String _name = "";
  String _id = "";
  String _symbol = "";
  int  _assetType = 1;
  int pageNum = 1;
  static const messageChannel = const BasicMessageChannel(
      'BasicMessageChannelPlugin', StandardMessageCodec());
  String _balance = "0";
  List<dynamic> _transactions = [] ;
  String txType = "all";
  void initState() {
    super.initState();
    _id = widget.id;
    _symbol = widget.symbol;
    _assetType = widget.assetType;
    receiveMessage();
    sendMessage();
  }
  Future<void> sendMessage() async {
    Map msg = new Map();
    msg["method"] = "get_wallet_address";
    String reply = (await messageChannel.send(msg)) as String;
    print("reply flutter receive2  = "+ reply);
    Map<String, dynamic> rs = convert.jsonDecode(reply);
    var method =  rs["method"];
    if(method == "get_wallet_address"){
      var address =  rs["address"];
      var name = rs["name"];
      this.setState(() {
        _address = address;
        _name = name;
      });
    }

    Map msg1 = new Map();
    msg1["method"] = "get_transaction_list";
    msg1["pageNum"] =  pageNum;
    msg1["assetId"] = _id;
    String reply1 = (await messageChannel.send(msg1)) as String;
    print("reply flutter receive1  = "+ reply1);


    Map msg2 = new Map();
    msg2["method"] = "get_asset_balance";
    msg2["assetId"] = _id;
    String reply2 = (await messageChannel.send(msg2)) as String;
    print("reply flutter receive222222  = "+ reply2);
    Map<String, dynamic> rs2 = convert.jsonDecode(reply2);
    var method2 =  rs2["method"];
    if(method2 == "get_asset_balance"){
      print("rs11111111111 ");
      print(rs2);
      print(rs2["asset"]["balance"]);
      this.setState(() {
        _balance = rs2["asset"]["balance"];
      });
    }
  }
  void receiveMessage(){
    messageChannel.setMessageHandler((result) async {
      print("asset detail  receiveMessage = "+ result.toString());
      //解析 原生发给 Flutter 的参数
      Map<String, dynamic> rs = convert.jsonDecode(result.toString());
      var method =  rs["method"];
      if(method == "get_asset_list"){
        if(mounted){
          List assetList =  rs["asset_list"];
          assetList.asMap().entries.map((entry){
            print(entry);
            if(entry.value["id"] == _id){
              print(entry.value["id"]);
              this.setState(() {
                print(entry.value["balance"]);
                 _balance = entry.value["balance"];
                _symbol = entry.value["symbol"];
              });
            }
          }).toList();
        }

      }else if(method == "get_transaction_list"){
        List<dynamic> transactions =  rs["transactions"];
        print("reply flutter transactions  = "+ transactions.toString());
        if(rs["asset_id"] == _id){
          this.setState(() {
            _transactions = transactions;
          });
        }

      }
      return 'Flutter 已收到消息';
    });

  }


  void refreshHistory(){
    print("refreshHistory  assetId = "+_id);
    (()async{
      Map msg = new Map();
      msg["method"] = "get_transaction_list";
      msg["pageNum"] = pageNum;
      msg["assetId"] = _id;
      String reply1 = (await messageChannel.send(msg)) as String;
      print("reply flutter receive1111  = "+ reply1);
    })();
  }
  void loadMore(){
    pageNum += 1;
    refreshHistory();
  }
  @override
  Widget build(BuildContext context) {
    return  WillPopScope(
        onWillPop: () async {
          Navigator.pop(context,"detail");
          return true;
    },
    child:
    new Scaffold(
        appBar: AppBar(
            backgroundColor: Colors.blue,
            centerTitle: true,
            elevation: 0.5,
            title: Text(
              _symbol,
              style: TextStyle(
                  fontFamily: "Sofia",
                  color: Colors.white
              ),
            ),
            leading: IconButton(
              icon: Icon(Icons.chevron_left),
              color: Colors.white,
              onPressed: () {
                Navigator.pop(context,"detail");
              },
            ),
            ),
        body: LayoutBuilder(
            builder: (context, constrains){
              return Container(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.start,
                  children: [
                    SizedBox(height: 10.0),
                    Padding(
                      padding:
                      const EdgeInsets.only(left: 10.0, right: 10.0, top: 8.0, bottom: 5.0),
                      child: Container(
                        height: 200.0,
                        width: 400.0,
                        decoration: BoxDecoration(
                            borderRadius: BorderRadius.all(Radius.circular(15.0))),
                        child: Material(
                          child: DecoratedBox(
                            decoration: BoxDecoration(
                              color: Colors.white,
                              borderRadius: BorderRadius.all(Radius.circular(15.0)),
                              image:  DecorationImage(image: AssetImage("assets/images/background.png"), fit: BoxFit.fill),
                              boxShadow: [
                                BoxShadow(
                                  color: Color(0xFFABABAB).withOpacity(0.7),
                                  blurRadius: 4.0,
                                  spreadRadius: 3.0,
                                ),
                              ],
                            ),
                            child: Container(
                              decoration: BoxDecoration(
                                borderRadius: BorderRadius.all(Radius.circular(15.0)),
                                color: Colors.black12.withOpacity(0.1),
                              ),
                              child:
                              Column(
                                children: <Widget>[
                                  Container(
                                    height: 50.0,
                                    width: 400.0,

                                    child: Center(
                                      child: Text(
                                        S.of(context).balance ,
                                        style:
                                        TextStyle(fontFamily: "Sofia", color: Colors.white, fontSize: 18.0),
                                      ),
                                    ),
                                  ),
                                  Container(
                                    height: 40.0,
                                    width: 400.0,

                                    child: Center(
                                      child: Text(
                                        _balance,
                                        style:
                                        TextStyle(fontFamily: "Sofia", color: Colors.white, fontSize: 30.0),
                                      ),
                                    ),
                                  ),
                                  Container(
                                    height: 40.0,
                                    width: 400.0,

                                    child: Center(
                                      child: Text(
                                        "≈ \$ 0.0",
                                        style:
                                        TextStyle(fontFamily: "Sofia", color: Colors.white, fontSize: 16.0),
                                      ),
                                    ),
                                  ),
                                  SizedBox(
                                    height: 15.0,
                                  ),
                                  Row(
                                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                                    children: [
                                      Column(
                                        children: <Widget>[
                                          Container(
                                            height: 20.0,
                                            width: 80.0,

                                            child: Center(
                                              child: Text(
                                                "0.00",
                                                style:
                                                TextStyle(fontFamily: "Sofia", color: Colors.white, fontSize: 18.0),
                                              ),
                                            ),
                                          ),
                                          Container(
                                            height: 20.0,
                                            width: 80.0,

                                            child: Center(
                                              child: Text(
                                                S.of(context).delegate1,
                                                style:
                                                TextStyle(fontFamily: "Sofia", color: Colors.white, fontSize: 16.0),
                                              ),
                                            ),
                                          ),
                                        ],
                                      ),
                                      Container(
                                        height: 30.0,
                                        width: 2.0,
                                        decoration: BoxDecoration(
                                            color: Colors.white54
                                        ),
                                      ),
                                      Column(
                                        children: <Widget>[
                                          Container(
                                            height: 20.0,
                                            width: 80.0,

                                            child: Center(
                                              child: Text(
                                                _balance,
                                                style:
                                                TextStyle(fontFamily: "Sofia", color: Colors.white, fontSize: 18.0),
                                              ),
                                            ),
                                          ),
                                          Container(
                                            height: 20.0,
                                            width: 80.0,

                                            child: Center(
                                              child: Text(
                                                S.of(context).available,
                                                style:
                                                TextStyle(fontFamily: "Sofia", color: Colors.white, fontSize: 16.0),
                                              ),
                                            ),
                                          ),
                                        ],
                                      ),
                                      Container(
                                        height: 30.0,
                                        width: 2.0,
                                        decoration: BoxDecoration(
                                            color: Colors.white54),
                                      ),
                                      Column(
                                        children: <Widget>[
                                          Container(
                                            height: 20.0,
                                            width: 80.0,
                                            child: Center(
                                              child: Text(
                                                "0.00",
                                                style:
                                                TextStyle(fontFamily: "Sofia", color: Colors.white, fontSize: 18.0),
                                              ),
                                            ),
                                          ),
                                          Container(
                                            height: 20.0,
                                            width: 80.0,
                                            child: Center(
                                              child: Text(
                                                S.of(context).locked,
                                                style:
                                                TextStyle(fontFamily: "Sofia", color: Colors.white, fontSize: 16.0),
                                              ),
                                            ),
                                          ),
                                        ],
                                      ),
                                    ],
                                  )
                                ],
                              ),
                            ),
                          ),
                        ),
                      ),
                    ),
                    SizedBox(height: 10.0),

                    Container(
                      height: constrains.maxHeight - 300,
                      decoration: BoxDecoration(
                        borderRadius: BorderRadius.all(Radius.circular(15.0)),
                        color: Colors.blue,
                      ),
                      child: new HistoryListView(_transactions, _address, refreshHistory, loadMore),
                    ),
                    SizedBox(height: 10.0),
                    Row(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [

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
                            child:Container(
                              height: 40.0,
                              width: 150.0,
                              decoration: BoxDecoration(
                                borderRadius: BorderRadius.all(Radius.circular(15.0)),
                                color: Colors.blue,
                              ),
                              child: Center(
                                child: Text(
                                  S.of(context).receive ,
                                  style:
                                  TextStyle(fontFamily: "Sofia", color: Colors.white, fontSize: 18.0),
                                ),
                              ),
                            ),
                          ),

                          InkWell(
                            onTap: () {
                              // Navigator.of(context).push(PageRouteBuilder(
                              //    pageBuilder: (_, __, ___) => new AssetDetailView()));
                              Navigator.push(
                                context,
                                MaterialPageRoute(builder: (context) {
                                  return SendView("", _id, _assetType, _symbol);
                                }),
                              ).then((data){
                                if(data == "success"){
                                  receiveMessage();
                                  sendMessage();
                                }
                              });
                            },
                            child:Container(
                              height: 40.0,
                              width: 150.0,
                              decoration: BoxDecoration(
                                borderRadius: BorderRadius.all(Radius.circular(15.0)),
                                color: Colors.blue,
                              ),
                              child: Center(
                                child: Text(
                                  S.of(context).send ,
                                  style:
                                  TextStyle(fontFamily: "Sofia", color: Colors.white, fontSize: 18.0),
                                ),
                              ),
                            ),
                          ),
                        ]
                    ),
                  ],
                ),
              );
            }
        )

      )
    );
  }
}