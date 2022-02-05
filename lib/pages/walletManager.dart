import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:platon_fans/generated/l10n.dart';
import 'package:platon_fans/pages/export.dart';
import 'dart:convert' as convert;
import 'package:fluttertoast/fluttertoast.dart';

import 'backup.dart';

class WalletManagerView extends StatefulWidget {
  String id;
  String walletName;
  WalletManagerView(this.id, this.walletName):super();
  WalletManagerViewState createState() => WalletManagerViewState();
}

class WalletManagerViewState extends State<WalletManagerView> {
  static const messageChannel = const BasicMessageChannel(
      'BasicMessageChannelPlugin', StandardMessageCodec());
  TextEditingController valueController = TextEditingController();
  TextEditingController newPwdController = TextEditingController();
  TextEditingController rePwdController = TextEditingController();
  late String _id;
  late String _walletName;
  void initState() {
    super.initState();
    _id = widget.id;
    _walletName = widget.walletName;
    print("walletid 1112123123 = ");

    print(_id);
  }
  Future<void> sendMessage(String type, String password, String newPassword, String rePassword) async {
    Map msg = new Map();
    msg["method"] = "check_password";
    msg["password"] = password;
    msg["walletId"] = _id;
    String reply = (await messageChannel.send(msg)) as String;
    Map<String, dynamic> rs = convert.jsonDecode(reply);
    print("reply flutter receive  = "+ reply);
    var method =  rs["method"];
    if(method == "check_password"){
      var status =  rs["status"];
      print("reply flutter status  = "+ status);
      if(status == "success"){
        if(type == "password"){
          if(newPassword != rePassword){
            Fluttertoast.showToast(
                msg: "两次密码不一致!",
                toastLength: Toast.LENGTH_SHORT,
                gravity: ToastGravity.CENTER,
                timeInSecForIosWeb: 1,
                backgroundColor: Colors.deepOrangeAccent,
                textColor: Colors.white,
                fontSize: 16.0
            );
          }else{
            Map msg2 = new Map();
            msg2["method"] = "change_password";
            msg2["password"] = password;
            msg2["newPassword"] = newPassword;
            msg2["walletId"] = _id;
            String reply2 = (await messageChannel.send(msg2)) as String;
            Map<String, dynamic> rs2 = convert.jsonDecode(reply2);
            print("reply flutter receive  = "+ reply2);
            var method =  rs2["method"];
            if(method == "change_password"){
              var status =  rs["status"];
              print("reply flutter2222 status  = "+ status);
              if(status == "success"){
                Fluttertoast.showToast(
                    msg: "修改成功。",
                    toastLength: Toast.LENGTH_SHORT,
                    gravity: ToastGravity.CENTER,
                    timeInSecForIosWeb: 1,
                    backgroundColor: Colors.blue,
                    textColor: Colors.white,
                    fontSize: 16.0
                );
              }
            }

          }

        }else if(type == "delete"){
          deleteWallet(password);
        }else if(type == "mnemonic"){
          Navigator.push(
            context,
            MaterialPageRoute(builder: (context) {
              return BackupView(password, _id);
            }),
          );

        }else{
          Navigator.push(
            context,
            MaterialPageRoute(builder: (context) {
              return ExportView(password, type, _id);
            }),
          );
        }
      }else{
        Fluttertoast.showToast(
            msg: "密码错误!",
            toastLength: Toast.LENGTH_SHORT,
            gravity: ToastGravity.CENTER,
            timeInSecForIosWeb: 1,
            backgroundColor: Colors.deepOrangeAccent,
            textColor: Colors.white,
            fontSize: 16.0
        );
      }

    }

  }
  Future<void> deleteWallet(String password) async {
    Map msg = new Map();
    msg["method"] = "delete_wallet";
    msg["password"] = password;
    msg["walletId"] = _id;
    String reply = (await messageChannel.send(msg)) as String;
    Map<String, dynamic> rs = convert.jsonDecode(reply);
    print("reply flutter receive  = "+ reply);
    var method =  rs["method"];
    if(method == "delete_wallet"){
      if(rs["status"] == "success"){
       /* Navigator.pushAndRemoveUntil(
          context,
          new MaterialPageRoute(builder: (context) => new CreateWalletView()),
              (route) => route == null,
        );

        */
      }

    }
  }
  @override
  Widget build(BuildContext context) {
    ///
    /// To measure the height of the device size
    ///
    return Scaffold(

        resizeToAvoidBottomInset: false,
        appBar: AppBar(
          backgroundColor: Colors.blue,
          centerTitle: true,
          elevation: 0.5,
          title: Text(
            _walletName,
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
        backgroundColor: Colors.white,

      body:Column(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: <Widget>[
          Column(
              children: <Widget>[

                InkWell(
                    onTap: () {
                      simpleDialog( valueController, "private_key");
                    },
                    child:
                        card("assets/images/avatar.png", "导出私钥",
                            ""),
                ),
                InkWell(
                  onTap: () {
                    simpleDialog( valueController, "mnemonic");
                  },
                  child:
                  card("assets/images/avatar.png", "导出助记词",
                      ""),
                ),
                InkWell(
                  onTap: () {
                    simpleDialog( valueController, "file");
                  },
                  child:
                  card("assets/images/avatar.png", "导出钱包文件",
                      ""),
                ),
                InkWell(
                  onTap: () {
                    simpleDialog( valueController, "password");
                  },
                  child:
                  card("assets/images/avatar.png", "修改钱包密码",
                      ""),
                ),


                SizedBox(
                  height: 10.0,
                ),

              ]),
            InkWell(
              onTap: () {
                simpleDialog( valueController, "delete");
              },
              child:
                Container(
                  height: 80.0,
                  width: MediaQuery.of(context).size.width,
                  color: Colors.white60,
                  alignment: Alignment.center,
                  child:
                  Text(
                    "删除钱包",
                    style:
                    TextStyle(fontFamily: "Sofia", color: Colors.red, fontSize: 20.0),
                  ),
                ),
            ),


              ]
      )

    );
  }

  ///
  /// Card under of tab bar
  ///
  Widget card(String img, String title, String subTitle) {
    return Padding(
      padding: const EdgeInsets.only(left: 20.0, right: 20.0, top:15),
      child: Container(
        height: 60.0,
        width: double.infinity,
        decoration: BoxDecoration(
            borderRadius: BorderRadius.all(Radius.circular(5.0)),
            color: Colors.white,
            boxShadow: [
              BoxShadow(
                  blurRadius: 5.0,
                  color: Colors.black12.withOpacity(0.1),
                  spreadRadius: 3.0),
            ]),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: <Widget>[
            Padding(
              padding: const EdgeInsets.only(left: 30.0),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: <Widget>[
                  Text(
                    title,
                    style: TextStyle(
                      fontFamily: "Popins",
                      color: Colors.black87,
                      fontSize: 18.0,
                    ),
                  ),

                ],
              ),
            ),
            Padding(
              padding: const EdgeInsets.only(right: 20.0),
              child:  Icon(Icons.chevron_right_outlined),
            ),
          ],
        ),
      ),
    );
  }

  void simpleDialog(TextEditingController valueController, String type) {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (BuildContext context) {
        return AlertDialog(
          title: Column(
            children: type == "delete" ? [
              Text(type == "password"  ? S.of(context).changePassword : S.of(context).inputPassword1 ,
                  style: TextStyle(
                      fontFamily: "Sofia",
                      fontWeight: FontWeight.w700,
                      fontSize: 18.0)),
              Text("请先做好钱包备份，再删除钱包！",
                  style: TextStyle(
                      fontFamily: "Sofia",
                      fontWeight: FontWeight.w400,
                      color: Colors.red,
                      fontSize: 18.0)),
            ]:[
              Text(type == "password" ?S.of(context).changePassword  : S.of(context).inputPassword1,
                  style: TextStyle(
                  fontFamily: "Sofia",
                  fontWeight: FontWeight.w700,
                  fontSize: 18.0)
              ),
            ],
          ),
          content:Container(
            height: type =="password" ? 200 : 80,
            child: Column(
              children: type =="password" ? [
              TextField(
                controller: valueController,
                obscureText: true,
                decoration: InputDecoration(
                  labelText: S.of(context).oldPassword ,
                ),
              ),
              TextField(
                controller: newPwdController,
                obscureText: true,
                decoration: InputDecoration(
                  labelText: S.of(context).newPassword ,
                ),
              ),
              TextField(
                controller: rePwdController,
                obscureText: true,
                decoration: InputDecoration(
                  labelText: S.of(context).confirmPassword,
                ),
              ),
              ] : [
            TextField(
            controller: valueController,
            obscureText: true,
            decoration: InputDecoration(
            labelText: S.of(context).password ,
            ),
          ),
          ],
        ),
          ),
          actions: <Widget>[
          TextButton(
              child:  Text(S.of(context).cancel, style: TextStyle(fontFamily: "Sofia")),
              onPressed: () {
                valueController.text = "";
                Navigator.of(context).pop();
              },
            ),
            TextButton(
              child:  Text(S.of(context).ok, style: TextStyle(fontFamily: "Sofia")),
              onPressed: () {
                String password = valueController.text;
                valueController.text = "";
                if(type == "password"){
                  String newPassword = newPwdController.text;
                  String rePassword = rePwdController.text;
                      newPwdController.text= "";
                      rePwdController.text = "";
                  sendMessage(type, password, newPassword, rePassword);
                }else{
                  sendMessage(type, password, "", "");
                }
                Navigator.of(context).pop();
              },
            )
          ],
        );
      },
    );
  }
}
