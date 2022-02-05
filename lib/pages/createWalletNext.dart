import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'dart:convert' as convert;
import 'package:flutter/services.dart';
import 'package:platon_fans/generated/l10n.dart';
import 'package:platon_fans/pages/backup.dart';

import 'package:fluttertoast/fluttertoast.dart';
import 'package:platon_fans/pages/home.dart';
class CreateWalletNextView extends StatefulWidget {
  int nodeId;
  CreateWalletNextView(this.nodeId) : super();
  CreateWalletNextViewState createState() => CreateWalletNextViewState();
}
class CreateWalletNextViewState extends State<CreateWalletNextView> {
  String name = "" ;
  String password = "";
  String re_password = "";
  Color buttonColor =  Colors.blue;
  bool submitted = false;

  late BuildContext dialogContext;
  late int _nodeId;
  static const messageChannel = const BasicMessageChannel(
      'BasicMessageChannelPlugin', StandardMessageCodec());
  void receiveMessage() {
    messageChannel.setMessageHandler((result) async {
      print("_basicMessageChannel createWallet receive  = "+ result.toString());

      Map<String, dynamic> rs = convert.jsonDecode(result.toString());
      var code =  rs["code"];
      if(code == 200){
        Future.delayed(Duration(seconds: 3),(){

          Navigator.pop(dialogContext);
          Navigator.push(
              context,
              MaterialPageRoute(builder: (context) {
                return BackupView(password,"");
              })
          ).then((data){
            Navigator.pushAndRemoveUntil(
              context,
              new MaterialPageRoute(builder: (context) => new Home()),
                  (route) => route == null,
            );
          });

        });

      }

      return 'Flutter 已收到消息';
    });
  }
  void initState() {
    super.initState();
    receiveMessage();
    this._nodeId = widget.nodeId;
  }

  @override
  Widget build(BuildContext context) {
    ///
    /// To measure the height of the device size
    ///
    return Scaffold(
      resizeToAvoidBottomInset: false,
      backgroundColor: Colors.white,
      appBar: AppBar(
        backgroundColor: Colors.blue,
        centerTitle: true,
        elevation: 0.5,
        title: Text(
          S.of(context).createWallet ,
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
      ),
      body: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        children: <Widget>[
          SizedBox(
            height: 25.0,
          ),
          Padding(
            padding:
            const EdgeInsets.only(left: 20.0, right: 20.0, top: 8.0, bottom: 5),
            child:Column(
                mainAxisAlignment: MainAxisAlignment.start,
                children: <Widget>[

                  SizedBox(height: 10.0),
                  TextField(
                    decoration: InputDecoration(
                      labelText: S.of(context).walletName,
                    ),
                    onChanged: (text){
                      //buttonStatus();
                      this.setState(() {
                        name = text;
                      });
                    },
                  ),
                  TextField(
                    obscureText: true,
                    decoration: InputDecoration(
                      labelText: S.of(context).password,
                    ),

                    onChanged: (text){
                      //buttonStatus();
                      this.setState(() {
                        password = text;
                      });
                    },
                  ),
                  TextField(
                    obscureText: true,
                    decoration: InputDecoration(
                      labelText: S.of(context).confirmPassword,
                    ),
                    onChanged: (text){
                      //buttonStatus();
                      this.setState(() {
                        re_password = text;
                      });
                    },
                  ),
                  SizedBox(height: 10,),
                  Container(

                    child: Text(
                        S.of(context).createWalletNote,
                      style: TextStyle(fontFamily: "Sofia", color: Colors.red, fontSize: 14.0),

                    ),
                  )


                ]
            ),
          ),

          SizedBox(
            height: 30.0,
          ),Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                ElevatedButton(
                  style: ButtonStyle(
                      textStyle: MaterialStateProperty.all(
                          TextStyle(fontSize: 18,)),
                      padding: MaterialStateProperty.all(EdgeInsets.only(left:30,right: 30,top:10,bottom: 10)),
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
                    S.of(context).createWallet,
                    style: TextStyle(color: Colors.white),
                  ),

                  onPressed: () {
                    submit();
                  },
                ),

              ]
          ),
        ],
      ),
    );
  }
  @override
  void dispose(){
    super.dispose();

  }
  void submit() async {
    if(submitted == true){
      return;
    }
    if(name == ""){
      showToast(S.of(context).inputWalletName,Colors.red);
      submitted = false;
      return;
    }
    if(password == ""){
      showToast(S.of(context).inputPassword,Colors.red);
      submitted = false;
      return;
    }
    if(password != re_password){
      showToast(S.of(context).confirmPassword,Colors.red);
      submitted = false;
      return;
    }



    Map msg1 = new Map();
    msg1["method"] = "check_wallet_name";
    msg1["name"] = name;
    String reply1 = (await messageChannel.send(msg1)) as String;
    Map<String, dynamic> rs1 = convert.jsonDecode(reply1);
    var method1 =  rs1["method"];
    if(method1 == "check_wallet_name"){
        if(rs1["rs"] == "1"){
          showToast(S.of(context).walletNameExits,Colors.red);
          submitted = false;
          return;
        }else{
          submitted = true;
          _showDialog();
          print("_basicMessageChannel flutter send");
          Map msg = new Map();
          msg["method"] = "wallet_create";
          msg["name"] = name;
          msg["password"] = password;
          msg["nodeId"] = _nodeId;
          String reply = (await messageChannel.send(msg)) as String;
          submitted = false;
        }
    }


  }

  void _showDialog(){
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
                      Text(S.of(context).createWallet+" ...",
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
  void buttonStatus(){
    if(name != ""&& password != "" && re_password!= ""){
      this.setState(() {
        buttonColor = Colors.blue;
      });
    }else{
      this.setState(() {
        buttonColor = Colors.black26;
      });
    }
  }
}