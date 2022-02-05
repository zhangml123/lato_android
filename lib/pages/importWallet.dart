import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:platon_fans/generated/l10n.dart';

import 'dart:convert' as convert;

import 'asset/contractTransaction.dart';
import 'home.dart';
class ImportWalletView extends StatefulWidget {
  int nodeId;
  ImportWalletView(this.nodeId);
  ImportWalletViewState createState() => ImportWalletViewState();
}

class ImportWalletViewState extends State<ImportWalletView> with SingleTickerProviderStateMixin{
  static const messageChannel = const BasicMessageChannel(
      'BasicMessageChannelPlugin', StandardMessageCodec());
  late TabController _tabController;
  TextEditingController privateKeyController = TextEditingController();
  TextEditingController mnemonicController = TextEditingController();
  TextEditingController keystoreController = TextEditingController();

  TextEditingController privateKeyNameController = TextEditingController();
  TextEditingController privateKeyPwdController = TextEditingController();
  TextEditingController privateKeyRPwdController = TextEditingController();


  TextEditingController mnemonicNameController = TextEditingController();
  TextEditingController mnemonicPwdController = TextEditingController();
  TextEditingController mnemonicRPwdController = TextEditingController();


  TextEditingController keystoreNameController = TextEditingController();
  TextEditingController keystorePwdController = TextEditingController();


  bool showPrivateKeyError = false;
  bool showPrivateKeyNameError = false;
  bool showPrivateKeyPwdError = false;
  bool showPrivateKeyRPwdError = false;

  bool showMnemonicError = false;
  bool showMnemonicNameError = false;
  bool showMnemonicPwdError = false;
  bool showMnemonicRPwdError = false;


  bool showKeystoreError = false;
  bool showKeystoreNameError = false;
  bool showKeystorePwdError = false;
  late BuildContext dialogContext;
  late int _nodeId;
  void initState() {
    super.initState();
    _tabController = TabController(length: 3, vsync: this);
    privateKeyController.addListener(() {
      if(privateKeyController.text != "") this.setState(() {
        showPrivateKeyError = false;
      });
      if(privateKeyNameController.text != "") this.setState(() {
        showPrivateKeyNameError = false;
      });
      if(privateKeyPwdController.text != "") this.setState(() {
        showPrivateKeyPwdError = false;
      });
      if(privateKeyRPwdController.text != "") this.setState(() {
        showPrivateKeyRPwdError = false;
      });



    });
    mnemonicController.addListener(() {
      if(mnemonicController.text != "") this.setState(() {
        showMnemonicError = false;
      });
    });
    keystoreController.addListener(() {
      if(keystoreController.text != "") this.setState(() {
        showKeystoreError = false;
      });
    });
    _nodeId = widget.nodeId;
  }
  @override
  void dispose() {
    super.dispose();
    _tabController.dispose();

  }
  void submit() async {
    int index = _tabController.index;
    switch(index){
      case 0 : importPrivateKey(); break;
      case 1 : importMnemonic(); break;
      case 2 : importKeystore(); break;

    }
  }

  void importPrivateKey(){
    String privateKey = privateKeyController.text;
    String name = privateKeyNameController.text;
    String password = privateKeyPwdController.text;
    String rePassword = privateKeyRPwdController.text;
    if(privateKey == ""){
      this.setState(() {
        showPrivateKeyError = true;
      });
      return;
    }

    if(privateKey!="" && name!="" && password!="" && rePassword == password){
      _showDialog();
      (() async {
        Map msg = new Map();
        msg["method"]     = "import_wallet";
        msg["type"]       = "privateKey";
        msg["privateKey"] = privateKey;
        msg["name"]       = name;
        msg["password"]   = password;
        msg["nodeId"]     = _nodeId;
        String reply = (await messageChannel.send(msg)) as String;
        print("import_wallet   reply= " + reply);
        Map<String, dynamic> rs = convert.jsonDecode(reply);
        var method = rs["method"];
        if (method == "import_wallet") {
            var status = rs["status"];
            if(status == "success"){

              Future.delayed(Duration(seconds: 3),(){
                Navigator.pop(dialogContext);
                Navigator.pushAndRemoveUntil(
                  context,
                  new MaterialPageRoute(builder: (context) => new Home()),
                      (route) => route == null,
                );
              });

            }
        }
      })();
    }


  }

  void importMnemonic(){
    String mnemonic = mnemonicController.text;
    String name = mnemonicNameController.text;
    String password = mnemonicPwdController.text;
    String rePassword = mnemonicRPwdController.text;
    if(mnemonic == ""){
      this.setState(() {
        showMnemonicError = true;
      });
      return;
    }
    if(mnemonic!="" && name!="" && password!="" && rePassword == password) {
      _showDialog();
      (() async {
        Map msg = new Map();
        msg["method"] = "import_wallet";
        msg["type"] = "mnemonic";
        msg["mnemonic"] = mnemonic;
        msg["name"] = name;
        msg["password"] = password;
        msg["nodeId"] = _nodeId;
        String reply = (await messageChannel.send(msg)) as String;
        print("import_wallet   reply= " + reply);
        Map<String, dynamic> rs = convert.jsonDecode(reply);
        var method = rs["method"];
        if (method == "import_wallet") {
          var status = rs["status"];
          if(status == "success"){

            Future.delayed(Duration(seconds: 3),(){
              Navigator.pop(dialogContext);
              Navigator.pushAndRemoveUntil(
                context,
                new MaterialPageRoute(builder: (context) => new Home()),
                    (route) => route == null,
              );
            });
          }
        }
      })();
    }
  }
  void importKeystore(){
    String keystore = keystoreController.text;
    String name = keystoreNameController.text;
    String password = keystorePwdController.text;
    print("keystore = "+keystore);
    print("name = "+name);
    print("password = "+password);

    if(keystore == ""){
      this.setState(() {
        showKeystoreError = true;
      });
      return;
    }
    if(keystore!="" && name!="" && password!="" ) {
      _showDialog();
      (() async {
        Map msg = new Map();
        msg["method"]   = "import_wallet";
        msg["type"]     = "keystore";
        msg["keystore"] = keystore;
        msg["name"]     = name;
        msg["password"] = password;
        msg["nodeId"]   = _nodeId;
        String reply = (await messageChannel.send(msg)) as String;
        print("import_wallet   reply= " + reply);
        Map<String, dynamic> rs = convert.jsonDecode(reply);
        var method = rs["method"];
        if (method == "import_wallet") {
          var status = rs["status"];
          if(status == "success"){

            Future.delayed(Duration(seconds: 3),(){
              Navigator.pop(dialogContext);
              Navigator.pushAndRemoveUntil(
                context,
                new MaterialPageRoute(builder: (context) => new Home()),
                    (route) => route == null,
              );
            });
          }
        }
      })();
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
                      Text(S.of(context).importWallet+"...",
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
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      resizeToAvoidBottomInset: false,
      backgroundColor: Colors.white,
      body:
      NestedScrollView(

          headerSliverBuilder: (BuildContext context, bool innerBoxIsScroller) {
            return <Widget>[
              SliverAppBar(
                title: Text(
                  S.of(context).importWallet,
                  style: TextStyle(fontFamily: "Sofia"),
                ),
                pinned: true,
                backgroundColor: Colors.blue,
                forceElevated: innerBoxIsScroller,
                centerTitle: true,
                bottom: TabBar(
                  indicatorColor: Colors.black54,
                  indicatorSize: TabBarIndicatorSize.tab,
                  indicatorWeight: 3,
                  tabs: [
                    Tab(icon: Text(S.of(context).importWallet,style: TextStyle(color: Colors.white))),
                    Tab(icon: Text(S.of(context).importMnemonic,style: TextStyle(color: Colors.white))),
                    Tab(icon: Text(S.of(context).importFile,style: TextStyle(color: Colors.white))),
                  ],
                  controller: _tabController,
                ),
              )
            ];
          },
          body:Column(
            children: [
              Container(
                height:500,
                child: TabBarView(
                  children: [
                    Align(
                        alignment: Alignment.topLeft,
                        child:
                        Container(
                          height: 500,
                          margin: const EdgeInsets.only(left: 10.0, right: 10.0, top: 10),
                          decoration: BoxDecoration(
                              borderRadius: BorderRadius.all(Radius.circular(5.0)),
                              color: Color(0xFFFAFAFA)
                          ),
                          padding: const EdgeInsets.only(left: 20.0, right: 20.0, top: 10),
                          child: Column(
                            children: [
                          TextField(
                            controller: privateKeyController,
                            maxLines: 2,
                            decoration: InputDecoration(
                              labelText: S.of(context).privateKey,
                              errorText: showPrivateKeyError ? S.of(context).enterPrivateKey:null,
                              /*border: OutlineInputBorder(
                          //borderRadius: BorderRadius.circular(15.0),
                        )*/
                            ),
                          ),SizedBox(height: 30,),
                              TextField(
                                controller: privateKeyNameController,
                                maxLines: 1,
                                decoration: InputDecoration(
                                  labelText:S.of(context).walletName ,
                                ),
                              ),
                              SizedBox(height: 15,),
                              TextField(
                                controller: privateKeyPwdController,
                                obscureText: true,
                                maxLines: 1,
                                decoration: InputDecoration(
                                  labelText: S.of(context).password ,
                                ),
                              ),
                              SizedBox(height: 15,),
                              TextField(
                                controller: privateKeyRPwdController,
                                obscureText: true,
                                maxLines: 1,
                                decoration: InputDecoration(
                                  labelText:S.of(context).confirmPassword ,
                                ),
                              ),

                            ])
                        )
                    ),
                    Align(
                        alignment: Alignment.topLeft,
                        child:
                        Container(
                            height: 500,
                            margin: const EdgeInsets.only(left: 10.0, right: 10.0, top: 10),
                            decoration: BoxDecoration(
                                borderRadius: BorderRadius.all(Radius.circular(5.0)),
                                color: Color(0xFFFAFAFA)
                            ),
                            padding: const EdgeInsets.only(left: 20.0, right: 20.0, top: 10),
                            child:

                            Column(
                                children: [

                                  TextField(
                                    controller: mnemonicController,
                                    maxLines: 4,
                                    decoration: InputDecoration(
                                      labelText: S.of(context).mnemonic ,
                                      errorText: showMnemonicError ?  S.of(context).enterMnemonic:null,
                                    ),


                                  ),
                                  SizedBox(height: 30,),
                                  TextField(
                                    controller: mnemonicNameController,
                                    // obscureText: true,
                                    maxLines: 1,
                                    decoration: InputDecoration(
                                      labelText: S.of(context).walletName
                                    ),
                                  ),
                                  SizedBox(height: 15,),
                                  TextField(
                                    controller: mnemonicPwdController,
                                    obscureText: true,
                                    maxLines: 1,
                                    decoration: InputDecoration(
                                      labelText: S.of(context).password,
                                    ),
                                  ),
                                  SizedBox(height: 15,),
                                  TextField(
                                    controller: mnemonicRPwdController,
                                    obscureText: true,
                                    maxLines: 1,
                                    decoration: InputDecoration(
                                      labelText: S.of(context).confirmPassword,
                                    ),
                                  ),

                                ])

                        )
                    ),
                    Align(
                        alignment: Alignment.topLeft,
                        child:
                        Container(
                            margin: const EdgeInsets.only(left: 10.0, right: 10.0, top: 10),
                            decoration: BoxDecoration(
                                borderRadius: BorderRadius.all(Radius.circular(5.0)),
                                color: Color(0xFFFAFAFA)
                            ),
                            padding: const EdgeInsets.only(left: 20.0, right: 20.0, top: 0),
                            child:
                            Column(
                              children: [
                                TextField(
                                  controller: keystoreController,
                                  maxLines: 5,
                                  decoration: InputDecoration(
                                    labelText: S.of(context).walletFile,
                                    errorText: showKeystoreError ?  S.of(context).enterWalletFile:null,
                                  ),

                                ),
                                SizedBox(height: 30,),
                                TextField(
                                  // obscureText: true,
                                  controller: keystoreNameController,
                                  maxLines: 1,
                                  decoration: InputDecoration(
                                    labelText: S.of(context).walletName,
                                  ),
                                ),
                                SizedBox(height: 15,),
                                TextField(
                                  controller: keystorePwdController,
                                  obscureText: true,
                                  maxLines: 1,
                                  decoration: InputDecoration(
                                    labelText: S.of(context).password,
                                  ),
                                ),
                              ],
                            )



                        )
                    ),
                  ],
                  controller: _tabController,
                ),
              ),
              ElevatedButton(
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
                  S.of(context).importWallet,
                  style: TextStyle(color: Colors.white),
                ),

                onPressed: () {
                  print(_tabController.index);
                  submit();
                },
              ),

            ],
          )
      )
    );
  }

}
