����   3J
 M �	 < �	 < �	 < �	 < � �
 � �	 < �	 < �	 < �	 < �
 < �
 � �
 � �
 < �
 < �
 < �
 < �	 < �	 < �
 < �
 < �	 < � �
 � �
 � �@       
 � �
  �
 � �
  �
  �
  �
  �
  �
  �
 � �
 < �
 < �
 � �
 � �
 < �
 � �
 < �
 � �
 � �
 � �
 < �
 < �
 < �	  �
 < �
 < �	 � �	 � �
 � � � � � � �
 < �
 � �
 � �
 M �
 M � �
 � �
 � �	 � �
 � �	 � 	 �	 �	 �
 <	 < x I y width height sprite LjavafxEngine/Sprite; scale F lives 	direction speed rotation points 	materials Ljava/util/EnumSet; 	Signature 2Ljava/util/EnumSet<LjavafxEngine/ObjectMaterial;>; <init> (IIF)V Code LineNumberTable LocalVariableTable this LjavafxEngine/GameObject; move (II)V jump wayX wayY StackMapTable jumpDirection distance 	setSprite (Ljava/lang/String;)V bg Ljava/lang/String; render ((Ljavafx/scene/canvas/GraphicsContext;)V r Ljavafx/scene/transform/Rotate; g %Ljavafx/scene/canvas/GraphicsContext; renderExtra update ()V updateObject getDirection ()I click getX getY getWidth setWidth (I)V setter 	getHeigth 	setHeigth checkColision (IIII)Z  (LjavafxEngine/GameObject;IIII)Z obj (LjavafxEngine/GameObject;)Z go2 checkColisionPoint (II)Z 	placeFree placeFreeDirection 	(IIIIII)Z placeMeetingMaterial <(IIIILjavafxEngine/ObjectMaterial;)LjavafxEngine/GameObject; material LjavafxEngine/ObjectMaterial; placeMeeting 1(IIIILjava/lang/String;)LjavafxEngine/GameObject; ex "Ljava/lang/ClassNotFoundException; objName type Ljava/lang/Class; �	 � 
checkHover ()Z setDirection getRotation setRotation setX setY getLives setLives getMaterials ()Ljava/util/EnumSet; 4()Ljava/util/EnumSet<LjavafxEngine/ObjectMaterial;>; 
SourceFile GameObject.java ` { X O Y O Z O [ O javafxEngine/ObjectMaterial
 \ ] N O P O U V � � � ~ � � � ~ � � Q O R O � � i h S T javafx/scene/transform/Rotate � ` { ! � ~ � ~"#$ { y t z { } ~%&' � � � �( � � � � �)*+,-./012 �34 javafxEngine/GameObject � �56789:;6  java/lang/ClassNotFoundException<=>?@ABCDEF OG VH OI V � � W O java/lang/Object java/util/Iterator java/lang/Class java/lang/String java/util/EnumSet noneOf &(Ljava/lang/Class;)Ljava/util/EnumSet; java/lang/Math atan (D)D 	toDegrees javafxEngine/Sprite getImage ()Ljavafx/scene/image/Image; javafx/scene/image/Image ()D 	getHeight (DDD)V #javafx/scene/canvas/GraphicsContext save getMxx getMyx getMxy getMyy getTx getTy setTransform 	(DDDDDD)V 	drawImage !(Ljavafx/scene/image/Image;DDDD)V restore 	toRadians cos sin Solid javafxEngine/JavaFXPlatformer room LjavafxEngine/Room; javafxEngine/Room objects Ljava/util/ArrayList; java/util/ArrayList iterator ()Ljava/util/Iterator; hasNext next ()Ljava/lang/Object; contains (Ljava/lang/Object;)Z forName %(Ljava/lang/String;)Ljava/lang/Class; getClass ()Ljava/lang/Class; equals getName ()Ljava/lang/String; java/util/logging/Logger 	getLogger .(Ljava/lang/String;)Ljava/util/logging/Logger; java/util/logging/Level SEVERE Ljava/util/logging/Level; log C(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Throwable;)V mouseX scaleX mouseY scaleY! < M     N O    P O    Q O    R O    S T    U V    W O    X O    Y O    Z O    [ O    \ ]  ^    _ "  ` a  b   �     1*� *� *� *� *� *� � *� 	*� 
*%� �    c   * 
   "   	        ! # & $ + % 0 & d   *    1 e f     1 N O    1 P O    1 U V   g h  b   Q     *� *� �    c       *  + 
 , d         e f      X O     Y O   i h  b   �     Y� � *Z� � 6*� � ,� � * �� � *� � *l�� � �� **� `� **� `� �    c   2    0  2  4  8  <  > # @ - D 5 G D I N J X K d        Y e f     Y j O    Y k O  l    	  m h  b       �� "**� 	*� 
*� *� � � 	*� Z� #**� 	*� 
*� *� � � 
*t�  �� #**� 	*� 
*� *� � � 
*t� � "**� 	*� 
*� *� � � 	*� �    c   & 	   O  P # Q B R I S i T p U � V � X d        � e f     � X O    � n O  l    #%&%  o p  b   5      �    c       \ d        e f      q r   s t  b       �*� � �*� � ]� Y*� t�*� 	�*� � �  oc��*� 
�*� � �  oc��� M+� +,�  ,� !,� ",� #,� $,� %� &+*� � *� �*� �*� '�*� (�� )*� � +� **+� +�    c   * 
   _  a  c H d L e h h � i � j � l � m d      H   u v    � e f     � w x  l    � h)  y t  b   5      �    c       r d        e f      w x  z {    | {  b  6     �*� � 
*� � ,*� � �*� -�     k          +   Z   ;   �   K     [*Y� 	*� `� 	� k*Y� 
*� d� 
� [*Y� 	*� d� 	� K*Y� 
*� `� 
� ;**� �*� �*� -�� .� /kc�� **� �*� �*� -�� .� 0kc�� *� 1�    c   B    x  z  |  ~ D � Q � T � a � d � q � t � � � � � � � � � � � d       � e f   l   	 57  } ~  b   /     *� �    c       � d        e f     {  b   +      �    c       � d        e f    � ~  b   /     *� 	�    c       � d        e f    � ~  b   /     *� 
�    c       � d        e f    � ~  b   <     *� � � *� �k��    c       � d        e f    � �  b   >     *� �    c   
    �  � d        e f      � O   � ~  b   <     *� � � *� �k��    c       � d        e f    � �  b   >     *� �    c   
    �  � d        e f      � O   � �  b   ]     **� 2�    c       � d   4     e f      N O     P O     Q O     R O   � �  b   �     6+� +� '`� *+� `� +� +� (`� +� `� � �    c       �  �  � & � d   >    6 e f     6 � f    6 N O    6 P O    6 Q O    6 R O  l    4@  � �  b   I     *+� +� +� '+� (� 3�    c       � d        e f      � f   � �  b   �     9*� � �*� *� '`� $*� � *� *� (`� *� � � �    c       �  � 	 �  �  � , � d        9 e f     9 N O    9 P O  l    	-@  � �  b   r     *� 4� 5� � �    c       � d   4     e f      N O     P O     Q O     R O  l    @  � �  b   �     U� *`� 6�Z� *d`� 6� �� *d� 6�� *`� 6��    c   & 	   �  �  �  � ) � 1 � > � F � S � d   H    U e f     U N O    U P O    U Q O    U R O    U X O    U n O  l      � �  b   �     D� 7� 8� 9:� : � 0� ; � <:� =� >� *� 2� �����    c       � ! � < � ? � B � d   H  !  � f    D e f     D N O    D P O    D Q O    D R O    D � �  l    �  �3�   � �  b  5  	   a� ?:� 7� 8� 9:� : � 0� ; � <:� @� A� *� 2� ���̧ :<� C� D� E� F�    E L B F I L B  c   & 	   �  � ( � C  F I L N _ d   \ 	 (  � f  N  � �    a e f     a N O    a P O    a Q O    a R O    a � r   E � �  l   $ �  � �3� �   � �  �  � �  b   A     *� G�� Hn�� I�� Jn�� K�    c      
 d        e f    � �  b   >     *� �    c   
     d        e f      X O   � ~  b   /     *� �    c       d        e f    � �  b   >     *� �    c   
      d        e f      Z O   � �  b   >     *� 	�    c   
   & ' d        e f      N O   � �  b   >     *� 
�    c   
   - . d        e f      P O   � ~  b   /     *� L�    c      4 d        e f    � �  b   >     *� L�    c   
   ; < d        e f      W O   � �  b   /     *� �    c      B d        e f   ^    �  �    �