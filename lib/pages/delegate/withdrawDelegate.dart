
import 'package:flutter/material.dart';
class WithdrawDelegateView extends StatefulWidget {
  @override
  WithdrawDelegateViewState createState() {
    return new WithdrawDelegateViewState();
  }
}

class WithdrawDelegateViewState extends State<WithdrawDelegateView> with SingleTickerProviderStateMixin {
  @override
  Widget build(BuildContext context) {
    return Scaffold(

        resizeToAvoidBottomInset: false,
        backgroundColor: Colors.white,
        appBar: AppBar(
          backgroundColor: Colors.blue,
          centerTitle: true,
          elevation: 0.5,
          title: Text(
            "委托",
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
        body: Column()
    );
  }
}
