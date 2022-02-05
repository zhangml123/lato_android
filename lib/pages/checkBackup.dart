import 'dart:ffi';

import 'package:flutter/material.dart';
import 'package:platon_fans/pages/about.dart';

import 'home.dart';

class CheckBackupView extends StatefulWidget {
  List mnemonic;
  CheckBackupView(this.mnemonic) :super();

  CheckBackupViewState createState() => CheckBackupViewState();
}

class CheckBackupViewState extends State<CheckBackupView> {
  List mnemonic = [];
  List _mnemonic = [];
  List mnemonicRandom  = [];
  void initState() {
    super.initState();
    mnemonic = widget.mnemonic;
    print(mnemonic);
    mnemonicRandom =  new List.from(mnemonic);
    mnemonicRandom.shuffle();
    print(mnemonic);
    print(mnemonicRandom);
  }

  @override
  Widget build(BuildContext context) {

    return Scaffold(
        backgroundColor: Colors.white,
        appBar: AppBar(
          backgroundColor: Colors.blue,
          centerTitle: true,
          elevation: 0.5,
          title: Text(
            "备份钱包",
            style: TextStyle(
              fontFamily: "Sofia",
            ),
          ),
        ),
        body: mnemonic.length == 12 ?
        Column(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              Padding(
                padding: const EdgeInsets.all(20.0),
                child:Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Container(
                        child: Text("抄写下方助记词",
                          style: TextStyle(color: Colors.black87,fontSize: 20),
                        ),
                      ),
                      SizedBox(height: 10,),
                      Container(
                        child: Text("助记词用于恢复和重置钱包密码，请把它抄写在纸上并存放在安全的地方。",style: TextStyle(color: Colors.black87,fontSize: 14)),
                      ),
                      SizedBox(height: 20,),
                      Column(
                        children:
                        mnemonic.asMap().keys.map((k)
                        {
                          if(k % 3 == 0 ){
                          return Row(
                            mainAxisAlignment: MainAxisAlignment.start,
                            crossAxisAlignment: CrossAxisAlignment.center,
                            children: mnemonic.asMap().keys.map((k1)
                            {
                                return k1 >= k && k1< k+3 ? InkWell (
                                    onTap: () {
                                      if(_mnemonic.length <= k1 || _mnemonic[k1] == "") return;
                                      this.setState(() {
                                        _mnemonic[k1] = "";
                                      });
                                    },
                                    child:
                                    Container(
                                        width:  MediaQuery.of(context).size.width /3.5,
                                        decoration: BoxDecoration(
                                            border: new Border(bottom : BorderSide(color:Colors.blue,width: 1))
                                        ),
                                        alignment: Alignment.center,
                                        padding: const EdgeInsets.only( top: 8,bottom: 8,left: 5,right: 5),
                                        child:Text(_mnemonic.length > k1  ? (_mnemonic[k1] != "" ? _mnemonic[k1] : (k1+1).toString() ): (k1+1).toString())
                                    )
                                ) : Center() ;

                            }
                            ).toList(),
                          );
                          }else{
                            return Row();
                          }
                          }
                        ).toList(),
                      ),
                      SizedBox(height: 20,),
                      Wrap(
                        children:  mnemonicRandom.asMap().keys.map((k)=>
                        InkWell(
                          onTap: () {
                            if(_mnemonic.contains(mnemonicRandom[k])) {
                              this.setState(() {
                                _mnemonic[_mnemonic.indexOf(mnemonicRandom[k])] = "";
                              });
                            }else{
                              this.setState(() {
                                if(_mnemonic.indexOf("") == -1){
                                  _mnemonic.add(mnemonicRandom[k]);
                                }else{
                                  _mnemonic[_mnemonic.indexOf("")] = mnemonicRandom[k];
                                }
                              });
                            }

                          },
                          child:
                          Container(
                            decoration: BoxDecoration(
                              color:_mnemonic.contains(mnemonicRandom[k]) ? Colors.blue : Colors.white,
                              border: new Border.all(color:Colors.blue,width: 1)
                            ),
                            margin:  const EdgeInsets.only( top: 8,bottom: 8,left: 0,right: 5),
                            padding: const EdgeInsets.only( top: 8,bottom: 8,left: 5,right: 5),
                            child:
                            Text(mnemonicRandom[k],
                              style: TextStyle(
                                  color: _mnemonic.contains(mnemonicRandom[k]) ? Colors.white : Colors.blue,
                                  fontSize: 16
                              ),
                            )
                          ),

                        )
                        ).toList(),
                      ),

                    ]),
              ),

              Column(
                  children:[
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
                      "完成备份",
                      style: TextStyle(color: Colors.white),
                    ),

                    onPressed: () {
                      print("onPressed11111");
                      print(mnemonic);

                      print(_mnemonic);
                      if(_mnemonic.toString() == mnemonic.toString()){
                        print("相同");
                        showDialog(
                          context: context,
                          barrierDismissible: false,
                          builder: (BuildContext context) {
                            return AlertDialog(
                              title: Container(
                                alignment: Alignment.center,
                                child: const Icon(
                                    IconData(0xe6e0, fontFamily: 'MyIcons'),
                                    size: 60,
                                    color: Colors.blue
                                ),
                              ),
                              content:
                              Container(
                                height: 140,
                                child: Column(
                                  children: [
                                    Text("备份成功",
                                        style: TextStyle(
                                            fontFamily: "Sofia",
                                            fontWeight: FontWeight.w700,
                                            color: Colors.black54,
                                            fontSize: 20.0)),
                                    Text("请妥善保存您的助记词。",
                                        style: TextStyle(
                                            fontFamily: "Sofia",
                                            color: Color(0xFF444444).withOpacity(1),
                                            fontSize: 16.0)),
                                    SizedBox(height: 20,),
                                    Center(
                                      child: ElevatedButton(
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
                                          backgroundColor:MaterialStateProperty.all(Colors.blue),

                                        ),
                                        child: Text(
                                          "确定",
                                          style: TextStyle(color: Colors.white),
                                        ),

                                        onPressed: () {
                                         //Navigator.pop(context);
                                          Navigator.pushAndRemoveUntil(
                                          context,
                                          new MaterialPageRoute(builder: (context) => new Home()),
                                          (route) => route == null,
                                          );
                                        },
                                      ),
                                    )

                                  ],
                                ),
                              ),
                            );
                          },
                        );

                      }else{
                        print("不相同");
                        showDialog(
                          context: context,
                          barrierDismissible: false,
                          builder: (BuildContext context) {
                            return AlertDialog(
                              title: Container(
                                alignment: Alignment.center,
                                child: const Icon(
                                    IconData(0xe6e0, fontFamily: 'MyIcons'),
                                    size: 60,
                                    color: Colors.red
                                ),
                              ),
                              content:
                              Container(
                                height: 140,
                                child: Column(
                                  children: [
                                    Text("备份失败",
                                        style: TextStyle(
                                            fontFamily: "Sofia",
                                            fontWeight: FontWeight.w700,
                                            color: Colors.black54,
                                            fontSize: 20.0)),
                                    Text("请检查助记词顺序是否正确！",
                                        style: TextStyle(
                                            fontFamily: "Sofia",

                                            color: Color(0xFF444444).withOpacity(1),
                                            fontSize: 16.0)),
                                    SizedBox(height: 20,),
                                    Center(
                                      child: ElevatedButton(
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
                                          backgroundColor:MaterialStateProperty.all(Colors.blue),

                                        ),
                                        child: Text(
                                          "确定",
                                          style: TextStyle(color: Colors.white),
                                        ),

                                        onPressed: () {
                                          Navigator.pop(context);
                                        },
                                      ),
                                    )

                                  ],
                                ),
                              ),
                            );
                          },
                        );
                      }

                    },
                  ),
                    SizedBox(height: 10,),
                  InkWell (
                  onTap: () {

                    this.setState(() {
                      _mnemonic = [];
                    });
                  },
                  child:
                    Container(
                        width:  MediaQuery.of(context).size.width /3.5,

                        alignment: Alignment.center,
                        padding: const EdgeInsets.only( top: 8,bottom: 8,left: 5,right: 5),
                        child:Text("清空",
                        style:  TextStyle(color: Colors.blue,fontSize: 16,fontWeight: FontWeight.w700,),)
                    ),),

                    SizedBox(height: 20,)

                  ])

            ]) : null
    );
  }


}
