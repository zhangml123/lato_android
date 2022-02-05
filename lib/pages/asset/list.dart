import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:platon_fans/generated/l10n.dart';
import 'package:platon_fans/pages/asset/detail.dart';
import 'package:flutter/services.dart';

import 'package:fluttertoast/fluttertoast.dart';
import 'dart:convert' as convert;

import '../messageListView.dart';
import 'manager.dart';
class AssetListView extends StatefulWidget {
  String balance;
  List assetList;
  bool newMessage;
  Function() refresh;

  AssetListView(this.balance, this.assetList, this.newMessage, this.refresh):super();
  @override
  AssetListViewState createState() {
    return new AssetListViewState();
  }
}

class AssetListViewState extends State<AssetListView>{
  static const messageChannel= const BasicMessageChannel(
      'BasicMessageChannelPlugin', StandardMessageCodec());

  TextEditingController addressController = TextEditingController();
  TextEditingController nameController = TextEditingController();
  TextEditingController symbolController = TextEditingController();
  String _balance = "0";
  List _assetList = [];
  bool isSetting = false;
  late bool _newMessage ;
  late String inputError ;
  late Function() _refresh;
  @override
  void initState() {
    super.initState();
    _balance = widget.balance;
    _assetList = widget.assetList;
    inputError = "";
    _newMessage = widget.newMessage;
    _refresh = widget.refresh;
    print("_newMessage = ");
    print(_newMessage);
   // receiveMessage();
   // sendMessage();

  }
  Future<void> sendMessage() async {
    Map msg = new Map();
    msg["method"] = "get_asset_balance";
    String reply = (await messageChannel.send(msg)) as String;
    print("reply flutter receive  = "+ reply);

  }
  void receiveMessage(){
    messageChannel.setMessageHandler((result) async {
      print("asset list  receiveMessage = "+ result.toString());
      //解析 原生发给 Flutter 的参数
      Map<String, dynamic> rs = convert.jsonDecode(result.toString());
      var method =  rs["method"];
      if(method == "get_asset_list"){
        var assetList =  rs["asset_list"];
        if(mounted){
          this.setState(() {
            _assetList = assetList;
          });
        }
      }
      return 'Flutter 已收到消息';
    });
  }
  void showAddDialog(){
    addressController.text = "";
    nameController.text = "";
    symbolController.text = "";
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (BuildContext context) {
        return StatefulBuilder(builder: (context, state){
          return AlertDialog(
            title: Text("添加资产",
                style: TextStyle(
                    fontFamily: "Sofia",
                    fontWeight: FontWeight.w700,
                    fontSize: 18.0)),
            content:
            Container(
              height: 230,
              child:Column(
                  mainAxisAlignment: MainAxisAlignment.start,
                  children: <Widget>[
                    SizedBox(height: 10.0),
                    TextField(
                      controller: addressController,
                      decoration: InputDecoration(
                        labelText: "合约地址",
                        errorText: inputError != "" ? inputError : null,
                        suffixIcon:
                        InkWell(
                            onTap: () {
                              searchAsset(state);
                            },
                            child:
                            const Icon(
                                Icons.search,
                                size: 25,
                                color: Colors.blue
                            )
                        ),
                      ),
                      onChanged: (text){
                        state((){
                          inputError = "";
                          nameController.text = "";
                          symbolController.text = "";
                        });

                      },
                    ),
                    TextField(
                      enabled: false,
                      controller: nameController,
                      decoration: InputDecoration(

                        labelText: "资产名称",
                      ),

                    ),
                    TextField(
                      enabled:false,
                      controller: symbolController,
                      decoration: InputDecoration(
                        labelText: "资产符号",
                      ),

                    ),
                    SizedBox(height: 10,),
                  ]
              ),),
            actions: <Widget>[
              FlatButton(
                child: const Text('取消', style: TextStyle(fontFamily: "Sofia")),
                onPressed: () {

                  Navigator.of(context).pop();
                },
              ),
              FlatButton(
                child: const Text('确定', style: TextStyle(fontFamily: "Sofia")),
                onPressed: () {
                  addAsset();
                },
              )
            ],
          );
        });
      },
    );
  }
  void showDeleteDialog(String id) {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (BuildContext context) {
        return AlertDialog(
          title: Text("删除资产？",
              style: TextStyle(
                  fontFamily: "Sofia",
                  fontWeight: FontWeight.w700,
                  fontSize: 18.0)),
          actions: <Widget>[
            FlatButton(
              child: const Text('取消', style: TextStyle(fontFamily: "Sofia")),
              onPressed: () {
                Navigator.of(context).pop();
              },
            ),
            FlatButton(
              child: const Text('确定', style: TextStyle(fontFamily: "Sofia")),
              onPressed: () {
                deleteAsset(id);
              },
            )
          ],
        );
      },
    );
  }
  Future<void> deleteAsset(String id ) async {
    Map msg = new Map();
    msg["method"] = "delete_asset";
    msg["assetId"] = id;
    String reply = (await messageChannel.send(msg)) as String;
    print("reply flutter receive  = "+ reply);
    Map<String, dynamic> rs = convert.jsonDecode(reply);


    if(rs["method"] == "delete_asset" && rs["code"] == "1"){
      print(rs["msg"]);
      Navigator.of(context).pop();
    }
  }
  Future<void> addAsset() async {
    if(nameController.text == ""  || symbolController.text== ""){
      return;
    }
    Map msg = new Map();
    msg["method"] = "add_asset";
    msg["contractAddress"] = addressController.text;
    String reply = (await messageChannel.send(msg)) as String;
    print("reply flutter receive  = "+ reply);
    Map<String, dynamic> rs = convert.jsonDecode(reply);
    if(rs["method"] == "add_asset" && rs["code"] == "1"){
      print(rs["msg"]);
      Navigator.of(context).pop();
    }
  }
  Future<void> searchAsset(Function state) async {
    String contractAddress  = addressController.text;
    if(contractAddress != ""){
      Map msg = new Map();
      msg["method"] = "search_asset";
      msg["contractAddress"] = contractAddress;
      String reply = (await messageChannel.send(msg)) as String;
      print("reply flutter receive  = "+ reply);
      Map<String, dynamic> rs = convert.jsonDecode(reply);
      if(rs["method"] == "search_asset"  ){
        if(rs["code"] == "1"){
          state((){
            nameController.text = rs["asset"]["name"];
            symbolController.text = rs["asset"]["symbol"];
          });
        }else {
          state((){
            inputError = rs["msg"];
          });
        }

      }

    }
  }
  void showToast(String msg, MaterialColor color ){
    Fluttertoast.showToast(
        msg: msg,
        toastLength: Toast.LENGTH_SHORT,
        gravity: ToastGravity.BOTTOM,
        timeInSecForIosWeb: 1,
        backgroundColor: Colors.white,
        textColor: color,
        fontSize: 16.0
    );
  }
  @override
  void didUpdateWidget(AssetListView oldWidget) {
    super.didUpdateWidget(oldWidget);
    print("didUpdateWidget AssetListView");
    if(oldWidget.balance != widget.balance){
      this.setState(() {
        _balance = widget.balance;
      });
    }
    if(oldWidget.assetList != widget.assetList){
      this.setState(() {
        _assetList = widget.assetList;
      });
    }

    if(oldWidget.newMessage != widget.newMessage){
      this.setState(() {
        _newMessage = widget.newMessage;
      });
    }


  }
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: SingleChildScrollView(
        child: Column(
          children: <Widget>[
            Container(
              height: 50.0,
              width: MediaQuery.of(context).size.width,
              child: Padding(
                padding:
                const EdgeInsets.only(left: 20.0, right: 0.0, top: 8.0, bottom: 5),
                child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: <Widget>[
                      Row(
                          children: <Widget>[
                            Container(
                              height: 20.0,
                              width: 5.0,
                              decoration: BoxDecoration(
                                  color: Colors.blueAccent
                              ),
                            ),
                            SizedBox(
                              width: 10.0,
                            ),

                            InkWell(
                                onTap: () {
                                  Navigator.push(
                                    context,
                                    MaterialPageRoute(builder: (context) {
                                      return MessageListView();
                                    }),
                                  ).then((data){

                                    receiveMessage();
                                    sendMessage();
                                    _refresh();
                                  });
                                },
                                child:
                                Container(
                                  child: Text(
                                    S.of(context).asset ,
                                    style:
                                    TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 20.0),
                                  ),
                                ),
                            ),
                            Align(
                              alignment: Alignment.topLeft,
                              child: _newMessage ?
                              Container(
                                width: 8,
                                height: 8,
                                margin: const EdgeInsets.only( top: 5,),
                                decoration: BoxDecoration(
                                  borderRadius: BorderRadius.all(Radius.circular(8.0)),
                                  color: Colors.red,
                                ),
                              ) : SizedBox(),
                            ),
                          ]
                      ),

                      Row(
                          mainAxisAlignment: MainAxisAlignment.start,
                          children: <Widget>[
                            isSetting ?
                              InkWell(
                                  onTap: () {
                                  showAddDialog();
                                  },
                                  child:
                                      Container(
                                        width: 40,
                                        height: 40,
                                        child: Icon(Icons.add_circle_outline,color: Colors.blue,),)

                              ) : SizedBox(),
                            InkWell(

                              onTap: () {
                               this.setState(() {
                                  isSetting = !isSetting;
                                });
                                /*Navigator.push(
                                  context,
                                  MaterialPageRoute(builder: (context) {
                                    return AssetManagerView();
                                  }),
                                );*/
                              },
                              child:Container(
                                width: 40,
                                height: 40,
                                margin: const EdgeInsets.only( right: 10.0,),
                                child: Icon(Icons.settings_rounded,color: isSetting ? Colors.blue : Colors.black54,),)
                            ),
                          ]
                      ),

                    ]
                ),
              ),
            ),
            Container(
              height: MediaQuery.of(context).size.height-360,
              width: MediaQuery.of(context).size.width,
              child:
                ListView.builder(
                  //controller: _controller,
                  itemCount: _assetList.length,
                  itemBuilder: (BuildContext context, int position) {
                    Map<String, dynamic> asset =  _assetList[position];
                    return  _list(position,
                        Icons.gamepad, asset["symbol"], asset["balance"], asset["id"], asset["assetType"]);
                  },
                ),
            ),
           /* _list(
                Icons.satellite, "DOT", "Monthly Payment", "0.00"),
            _list(Icons.airport_shuttle, "DOT",
                "Monthly Payment", "0.00"),
            _list(
                Icons.code, "USDT", "Monthly Payment", "0.00"),
            _list(
                Icons.golf_course, "LDOT", "Monthly Payment", "0.00"),
            _list(
                Icons.phone, "aUSD", "Monthly Payment", "0.00"),*/
          ],
        ),
      ),
    );
  }
  Widget _list(int position, IconData icon, String symbol,  String value, String id, int assetType) {
    return Padding(
      padding: const EdgeInsets.only(left: 15.0, right: 15.0, bottom: 20.0),
      child:
      Container(
        height: 70.0,
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
        Padding(
        padding: const EdgeInsets.only(left: 15.0, right: 15.0, top: 15.0),
        child:
          InkWell(
            onTap: () {
             // Navigator.of(context).push(PageRouteBuilder(
              //    pageBuilder: (_, __, ___) => new AssetDetailView()));
              Navigator.push(
                context,
                MaterialPageRoute(builder: (context) {
                  return AssetDetailView(id, symbol, assetType);
                }),
              ).then((data){
                print("reply asset list back data = "+data);
                receiveMessage();
                sendMessage();
              });
            },
            child:Column(
              children: <Widget>[
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  crossAxisAlignment: CrossAxisAlignment.center,
                  children: <Widget>[
                    Row(
                      children: <Widget>[
                        Container(
                          height: 40.0,
                          width: 40.0,
                          decoration: BoxDecoration(
                            borderRadius: BorderRadius.all(Radius.circular(40.0)),
                            color: Colors.indigo,
                          ),
                          child: Center(
                            child: Image(
                              width:40,
                              height:40,
                              image:  AssetImage("assets/images/main.png") ,
                            ),
                          ),
                        ),
                        SizedBox(
                          width: 12.0,
                        ),

                        Text(
                          symbol,
                          style: TextStyle(
                              color: Colors.black,
                              fontFamily: "Popins",
                              fontWeight: FontWeight.w600,
                              fontSize: 15.5),
                        ),


                      ],
                    ),
                    Row(
                      children: <Widget>[
                    Text(
                      value,
                      style: TextStyle(
                        color: Colors.deepOrangeAccent,
                        fontFamily: "Popins",
                        fontWeight: FontWeight.w700,
                        fontSize: 15.5,
                      ),
                    ),
                    isSetting && position != 0?
                    InkWell(
                        onTap: () {
                          showDeleteDialog(id);
                        },
                        child:Container(
                          width: 40,
                          height: 40,
                          margin: const EdgeInsets.only( right: 0,),
                          child: Icon(Icons.remove_circle,color:Colors.redAccent ),)
                    ):SizedBox(),
                    ])

                  ],
                ),
              ],
            ),
          ),
        )
      ),
    );
  }

}