import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'dart:convert' as convert;
class ExportView extends StatefulWidget {
  String password;
  String type;
  String walletId;
  ExportView(this.password, this.type, this.walletId):super();
  ExportViewState createState() => ExportViewState();
}

class ExportViewState extends State<ExportView> {
  static const messageChannel = const BasicMessageChannel(
      'BasicMessageChannelPlugin', StandardMessageCodec());
  String _password = "";
  String _privateKey = "";
  String _type = "";
  String title = "";
  String _walletId = "";
  void initState() {
    super.initState();
    _password = widget.password;
    _type = widget.type;
    _walletId = widget.walletId;

    if(_type == "private_key"){
      title = "导出私钥";
      sendMessage("get_private_key");
    }else if(_type == "mnemonic"){
      title = "导出助记词";
      sendMessage("get_mnemonic");
    }else if(_type == "file") {
      title = "导出钱包文件";
      sendMessage("get_file");
    }

  }
  Future<void> sendMessage(String _method) async {
    Map msg = new Map();
    msg["method"] = _method;
    msg["password"] = _password;
    msg["walletId"] = _walletId;
    String reply = (await messageChannel.send(msg)) as String;
    Map<String, dynamic> rs = convert.jsonDecode(reply);
    print("reply flutter receive  = "+ reply);
    var method =  rs["method"];
    if(method == "get_private_key"){
      var status =  rs["status"];
      var privateKey =  rs["private_key"];
      if(status == "success"){
        this.setState(() {
          _privateKey = privateKey;
        });
      }
    }else if(method == "get_mnemonic"){
      var status =  rs["status"];
      var mnemonic =  rs["mnemonic"];
      if(status == "success"){
        this.setState(() {
          _privateKey = mnemonic;
        });
      }
    }else if(method == "get_file"){
      var status =  rs["status"];
      var keystore =  rs["keystore"];
      if(status == "success"){
        this.setState(() {
          _privateKey = convert.jsonEncode(keystore);
        });
      }
    }
  }
  @override
  Widget build(BuildContext context) {
    ///
    /// To measure the height of the device size
    ///
    return new Scaffold(
        appBar: AppBar(
        backgroundColor: Colors.indigo,
        centerTitle: true,
        elevation: 0.5,
        title: Text(
          title,
          style: TextStyle(
          fontFamily: "Sofia",

          ),
        ),
        leading: IconButton(
          icon: Icon(Icons.chevron_left),
          onPressed: () {
            Navigator.pop(context);
          },
        ),
        actions: <Widget>[
          PopupMenuButton<String>(
            itemBuilder: (context) => [
              PopupMenuItem(
                value: "Profile",
                child: Text("Profile"),
              ),
              PopupMenuItem(
                value: "Menu",
                child: Text("Menu"),
              ),
              PopupMenuItem(
                value: "Search",
                child: Text("Search"),
              ),
            ],
          )
        ]
        ),
        body:Column(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: <Widget>[
            Padding(
              padding: const EdgeInsets.only(left: 15.0, right: 15.0, top: 25),
                child:
                Column(
                  mainAxisAlignment: MainAxisAlignment.start,
                  children: <Widget>[
                    Container(
                      alignment: Alignment.topLeft,
                      height: 30.0,
                      width: MediaQuery.of(context).size.width,
                      child: Text(
                        "注意：请勿截图或录屏，请妥善保管您的私钥！",
                        style:
                        TextStyle(fontFamily: "Sofia", color: Colors.deepOrange, fontSize: 14.0),
                      ),

                    ),
                    Container(
                      decoration: BoxDecoration(
                        borderRadius: BorderRadius.all(Radius.circular(5.0)),
                        boxShadow: [
                          BoxShadow(
                            color: Colors.black12,

                          ),
                        ],
                      ),
                      alignment: Alignment.center,
                      width: MediaQuery.of(context).size.width,
                      child:
                      Padding(
                        padding: const EdgeInsets.only(left: 15.0, right: 15.0, top: 15, bottom: 15 ),
                        child:
                        Text(
                          _privateKey,
                          style:
                          TextStyle(fontFamily: "Sofia", color: Colors.black54, fontSize: 20.0),
                        ),
                      ),

                    ),
                  ]),


            ),
            InkWell(
              onTap: () {
                Clipboard.setData(ClipboardData(text: _privateKey));
                Fluttertoast.showToast(
                    msg: "复制成功",
                    toastLength: Toast.LENGTH_SHORT,
                    gravity: ToastGravity.CENTER,
                    timeInSecForIosWeb: 1,
                    backgroundColor: Colors.white,
                    textColor: Colors.blue,
                    fontSize: 16.0
                );
              },
              child:
                Padding(
                  padding: const EdgeInsets.only(left: 0.0, right: 0.0, bottom: 50),
                  child:
                  Container(
                    height: 40.0,
                    width: 150.0,
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.all(Radius.circular(15.0)),
                      color: Colors.blue,
                    ),

                    child: Center(
                      child: Text(
                        "复制",
                        style:
                        TextStyle(fontFamily: "Sofia", color: Colors.white, fontSize: 18.0),
                      ),
                    ),
                  ),
                )

            ),



          ]
        )
    );
  }
}
