����   3 i
  = >
  =	  ?	  @@Y      	  A	  B C
 
 = D
 
 E
 
 F G
 
 H
 I J K
  L
  M
  N	  O
  P
 Q R S T images Ljava/util/ArrayList; 	Signature 1Ljava/util/ArrayList<Ljavafx/scene/image/Image;>; index I speed D animate Z lastTime J <init> (Ljava/lang/String;)V Code LineNumberTable LocalVariableTable stream Ljava/io/InputStream; this LjavafxEngine/Sprite; name Ljava/lang/String; i StackMapTable S U V getImage ()Ljavafx/scene/image/Image; update ()V 
SourceFile Sprite.java ' : java/util/ArrayList      ! " % & java/lang/StringBuilder res/textures/ W X W Y .png Z [ \ ] ^ javafx/scene/image/Image ' _ ` a b c # $ d e f g h javafxEngine/Sprite java/lang/Object java/lang/String java/io/InputStream append -(Ljava/lang/String;)Ljava/lang/StringBuilder; (I)Ljava/lang/StringBuilder; toString ()Ljava/lang/String; java/lang/ClassLoader getSystemResourceAsStream )(Ljava/lang/String;)Ljava/io/InputStream; (Ljava/io/InputStream;)V add (Ljava/lang/Object;)Z size ()I get (I)Ljava/lang/Object; java/lang/System currentTimeMillis ()J !                      ! "    # $    % &     ' (  )  %     t*� *� Y� � *� * � *	� 	=� 
Y� � +� � � � � N-� � *� � Y-� � W����**� � � � � �    *   :      	          "  B  F  I  Y  \  _  s ! +   *  B  , -    t . /     t 0 1  " R 2    3   / � "  4 5  � & 6� O 4�    4 5  4  7 8  )   9     *� *� � � �    *       % +        . /    9 :  )   �     =*� � 8� *� 	e�*� �� '*� � 	*Y� `� *� *� � � *� �    *       *  ,  - ) . 7 / < 1 +       = . /   3    <  ;    <