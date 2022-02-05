import 'package:flutter/material.dart';
import 'package:platon_fans/generated/l10n.dart';
import 'package:platon_fans/pages/about.dart';
import 'package:platon_fans/pages/backupNext.dart';
import 'dart:convert' as convert;
import 'package:flutter/services.dart';
class BackupView extends StatefulWidget {
  String password;
  String walletId;
  BackupView(this.password, this.walletId) : super();
  BackupViewState createState() => BackupViewState();
}

class BackupViewState extends State<BackupView> {
  String _password = "";
  String _mnemonic = "";
  String _walletId = "";
  static const messageChannel = const BasicMessageChannel(
      'BasicMessageChannelPlugin', StandardMessageCodec());

  void initState() {
    super.initState();
    _password = widget.password;
    _walletId = widget.walletId;
    sendMessage();
  }
  Future<void> sendMessage() async {
    Map msg = new Map();
    msg["method"] = "get_mnemonic";
    msg["password"] = _password;
    msg["walletId"] = _walletId;
    String reply = (await messageChannel.send(msg)) as String;
    print("reply flutter receive  = "+ reply);
    Map<String, dynamic> rs = convert.jsonDecode(reply);
    var method =  rs["method"];
    if(method == "get_mnemonic"){
      var status =  rs["status"];
      var mnemonic =  rs["mnemonic"];
      print("mnemonic  = "+ mnemonic);
      this.setState(() {
        _mnemonic = mnemonic;
      });
    }
  }
  @override
  Widget build(BuildContext context) {
    ///
    /// To measure the height of the device size
    ///
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: AppBar(
        backgroundColor: Colors.blue,
        centerTitle: true,
        elevation: 0.5,
        title: Text(
          S.of(context).addWallet,

          style: TextStyle(
            fontFamily: "Sofia",
          ),
        ),
      ),
      body:
      Column(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            Padding(
              padding: const EdgeInsets.all(20.0),
              child: Column(
                  mainAxisAlignment: MainAxisAlignment.start,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Container(
                      child: Text(S.of(context).backupProm,
                        style: TextStyle(color: Colors.black87,fontSize: 20),
                      ),
                    ),
                    SizedBox(height: 10,),
                    Container(
                      child: Text(S.of(context).backupPromTxt,style: TextStyle(color: Colors.black87,fontSize: 14)),
                    ),
                    SizedBox(height: 20,),
                    Container(
                      child: Text(S.of(context).backupMnemonic,
                        style: TextStyle(color: Colors.black87,fontSize: 20),),

                    ),
                    SizedBox(height: 10,),
                    Container(
                      child: Text(S.of(context).backupMnemonicTxt1,style: TextStyle(color: Colors.black87,fontSize: 14)),
                    ),
                    SizedBox(height: 5,),
                    Container(
                      child: Text(S.of(context).backupMnemonicTxt2,style: TextStyle(color: Colors.black87,fontSize: 14)),
                    ),
                    SizedBox(height: 20,),
                    Container(
                      child: Text(S.of(context).offlineStorage,
                        style: TextStyle(color: Colors.black87,fontSize: 20),),
                    ),
                    SizedBox(height: 10,),
                    Container(
                      child: Text(S.of(context).offlineStorageTxt1,style: TextStyle(color: Colors.black87,fontSize: 14)),
                    ),
                    SizedBox(height: 5,),
                    Container(
                      child: Text(S.of(context).offlineStorageTxt2,style: TextStyle(color: Colors.black87,fontSize: 14)),
                    ),
                  ]),
            ),
            Column(
              children:[ ElevatedButton(
                style: ButtonStyle(
                    textStyle: MaterialStateProperty.all(
                        TextStyle(fontSize: 18,)),
                    padding: MaterialStateProperty.all(EdgeInsets.only(left:100,right: 100,top:10,bottom: 10)),

                    shape: MaterialStateProperty.all(
                        StadiumBorder(
                            side: BorderSide(
                              //设置 界面效果
                              style: BorderStyle.solid,
                              color: Colors.blue,
                            )
                        )
                    )

                ),
                child: Text(
                  "下一步",
                  style: TextStyle(color: Colors.white),
                ),

                onPressed: () {
                  showDialog(
                    context: context,
                    barrierDismissible: false,
                    builder: (BuildContext context) {
                      return AlertDialog(
                        title: Container(
                          //width: 60,
                          //height: 100,
                          //color: Colors.blue,
                          alignment: Alignment.center,
                          child:const Icon(
                              IconData(0xe6e0, fontFamily: 'MyIcons'),
                              size: 60,
                              color: Colors.blue
                          ),
                        ),
                        content:
                        Container(
                          height: 170,
                          child: Column(
                            children: [
                              Text("请勿截屏",
                                  style: TextStyle(
                                      fontFamily: "Sofia",
                                      fontWeight: FontWeight.w700,
                                      color: Colors.black54,
                                      fontSize: 20.0)),
                              Text("请勿截屏分享和储存，这将可能被第三方恶意软件收集，造成资产损失！",
                                  style: TextStyle(
                                      fontFamily: "Sofia",

                                      color: Color(0xFF444444).withOpacity(1),
                                      fontSize: 16.0)),
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
                                      Navigator.pop(context);
                                      Navigator.push(
                                        context,
                                        MaterialPageRoute(builder: (context) {
                                          return BackupNextView(_mnemonic);
                                        }),
                                      );
                                    },
                                  ),
                                ],
                              )
                            ],
                          ),
                        ),
                      );
                    },
                  );//Icons.warning_amber_sharp
                  /*Navigator.push(
                              context,
                              MaterialPageRoute(builder: (context) {
                                return CreateWalletNextView(_nodeId);
                              }),
                            );*/
                },
              ),
            SizedBox(height: 20,)

            ])

          ])
    );
  }


}
