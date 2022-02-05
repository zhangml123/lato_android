import 'package:flutter/material.dart';
import 'package:platon_fans/pages/about.dart';
import 'package:platon_fans/pages/checkBackup.dart';

class BackupNextView extends StatefulWidget {
  String mnemonic;
  BackupNextView(this.mnemonic) : super();
  BackupNextViewState createState() => BackupNextViewState();
}

class BackupNextViewState extends State<BackupNextView> {
  List mnemonic = [];
  void initState() {
    super.initState();
    mnemonic = widget.mnemonic.split(' ');
    print("mnemonic111=");
    print(mnemonic);

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
                        children: [
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            crossAxisAlignment: CrossAxisAlignment.center,
                            children: [
                              Container(
                                width:  MediaQuery.of(context).size.width /3.5,
                                decoration: BoxDecoration(
                                  //color: Color(0xFFEEEEEE),

                                  color: Colors.black12,
                                ),
                                alignment: Alignment.center,
                                padding: const EdgeInsets.only( top: 8,bottom: 8,left: 5,right: 5),
                                child:Text(mnemonic[0],style: TextStyle(color: Colors.black87,fontSize: 16),)
                              ),
                              Container(
                                  width:  MediaQuery.of(context).size.width /3.5,

                                  decoration: BoxDecoration(
                                    color: Colors.black12,
                                  ),
                                  alignment: Alignment.center,

                                  padding: const EdgeInsets.only( top: 8,bottom: 8,left: 5,right: 5),
                                  child:Text(mnemonic[1],style: TextStyle(color: Colors.black87,fontSize: 16),)
                              ),
                              Container(
                                  width:  MediaQuery.of(context).size.width /3.5,

                                  decoration: BoxDecoration(
                                    color: Colors.black12,
                                  ),
                                  alignment: Alignment.center,

                                  padding: const EdgeInsets.only( top: 8,bottom: 8,left: 5,right: 5),
                                  child:Text(mnemonic[2],style: TextStyle(color: Colors.black87,fontSize: 16),)
                              ),
                            ],
                          ),
                          SizedBox(height: MediaQuery.of(context).size.width * 0.025,),
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            crossAxisAlignment: CrossAxisAlignment.center,
                            children: [
                              Container(
                                  width:  MediaQuery.of(context).size.width /3.5,
                                  decoration: BoxDecoration(
                                    color: Colors.black12,
                                  ),
                                  alignment: Alignment.center,

                                  padding: const EdgeInsets.only( top: 8,bottom: 8,left: 5,right: 5),
                                  child:Text(mnemonic[3],style: TextStyle(color: Colors.black87,fontSize: 16),)
                              ),
                              Container(
                                  width:  MediaQuery.of(context).size.width /3.5,
                                  decoration: BoxDecoration(
                                    color: Colors.black12,
                                  ),
                                  alignment: Alignment.center,

                                  padding: const EdgeInsets.only( top: 8,bottom: 8,left: 5,right: 5),
                                  child:Text(mnemonic[4],style: TextStyle(color: Colors.black87,fontSize: 16),)
                              ),
                              Container(
                                  width:  MediaQuery.of(context).size.width /3.5,
                                  decoration: BoxDecoration(
                                    color: Colors.black12,
                                  ),
                                  alignment: Alignment.center,

                                  padding: const EdgeInsets.only( top: 8,bottom: 8,left: 5,right: 5),
                                  child:Text(mnemonic[5],style: TextStyle(color: Colors.black87,fontSize: 16),)
                              ),
                            ],
                          ),
                          SizedBox(height: MediaQuery.of(context).size.width * 0.025,),
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            crossAxisAlignment: CrossAxisAlignment.center,
                            children: [
                              Container(
                                  width:  MediaQuery.of(context).size.width /3.5,
                                  decoration: BoxDecoration(
                                    color: Colors.black12,
                                  ),
                                  alignment: Alignment.center,

                                  padding: const EdgeInsets.only( top: 8,bottom: 8,left: 5,right: 5),
                                  child:Text(mnemonic[6],style: TextStyle(color: Colors.black87,fontSize: 16),)
                              ),
                              Container(
                                  width:  MediaQuery.of(context).size.width /3.5,
                                  decoration: BoxDecoration(
                                    color: Colors.black12,
                                  ),
                                  alignment: Alignment.center,

                                  padding: const EdgeInsets.only( top: 8,bottom: 8,left: 5,right: 5),
                                  child:Text(mnemonic[7],style: TextStyle(color: Colors.black87,fontSize: 16),)
                              ),
                              Container(
                                  width:  MediaQuery.of(context).size.width /3.5,
                                  decoration: BoxDecoration(
                                    color: Colors.black12,
                                  ),
                                  alignment: Alignment.center,

                                  padding: const EdgeInsets.only( top: 8,bottom: 8,left: 5,right: 5),
                                  child:Text(mnemonic[8],style: TextStyle(color: Colors.black87,fontSize: 16),)
                              ),
                            ],
                          ),
                          SizedBox(height: MediaQuery.of(context).size.width * 0.025,),
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            crossAxisAlignment: CrossAxisAlignment.center,
                            children: [
                              Container(
                                  width:  MediaQuery.of(context).size.width / 3.5,
                                  decoration: BoxDecoration(
                                    color: Colors.black12,
                                  ),
                                  alignment: Alignment.center,

                                  padding: const EdgeInsets.only( top: 8,bottom: 8,left: 5,right: 5),
                                  child:Text(mnemonic[9],style: TextStyle(color: Colors.black87,fontSize: 16),)
                              ),
                              Container(
                                  width:  MediaQuery.of(context).size.width /3.5,
                                  decoration: BoxDecoration(
                                    color: Colors.black12,
                                  ),
                                  alignment: Alignment.center,

                                  padding: const EdgeInsets.only( top: 8,bottom: 8,left: 5,right: 5),
                                  child:Text(mnemonic[10],style: TextStyle(color: Colors.black87,fontSize: 16),)
                              ),
                              Container(
                                  width:  MediaQuery.of(context).size.width /3.5,
                                  decoration: BoxDecoration(
                                    color: Colors.black12,
                                  ),
                                  alignment: Alignment.center,

                                  padding: const EdgeInsets.only( top: 8,bottom: 8,left: 5,right: 5),
                                  child:Text(mnemonic[11],style: TextStyle(color: Colors.black87,fontSize: 16),)
                              ),
                            ],
                          )
                        ],
                      )
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
                      Navigator.pop(context);
                      Navigator.push(
                          context,
                          MaterialPageRoute(builder: (context) {
                            return CheckBackupView(mnemonic);
                          }),
                        );
                    },
                  ),
                    SizedBox(height: 20,)

                  ])

            ]) : null
    );
  }


}
