����   3 t
  ; <
  ;	  = >
  ; ?
  @
  A
 B C	 D E
 F A
 G H I
  J
  K
  L
  M
  N
  O
  P Q R Q S
  T U V loopingSounds Ljava/util/ArrayList; 	Signature 5Ljava/util/ArrayList<Ljavafx/scene/media/AudioClip;>; <init> ()V Code LineNumberTable LocalVariableTable this LjavafxEngine/AudioManager; 	playSound (Ljava/lang/String;ZF)V name Ljava/lang/String; loop Z volume F url Ljava/net/URL; sound Ljavafx/scene/media/AudioClip; StackMapTable W I (Ljava/lang/String;Z)V stopAll audio X 
SourceFile AudioManager.java    java/util/ArrayList   java/lang/StringBuilder res/sounds/ Y Z [ \ ] ^ _ ` a b W c d e javafx/scene/media/AudioClip  e f g h i j k l   & ' m n X o p q r s   javafxEngine/AudioManager java/lang/Object java/net/URL java/util/Iterator append -(Ljava/lang/String;)Ljava/lang/StringBuilder; toString ()Ljava/lang/String; java/lang/ClassLoader getSystemResource "(Ljava/lang/String;)Ljava/net/URL; java/lang/System out Ljava/io/PrintStream; java/io/PrintStream println (Ljava/lang/String;)V 	setVolume (D)V add (Ljava/lang/Object;)Z setCycleCount (I)V play iterator ()Ljava/util/Iterator; hasNext ()Z next ()Ljava/lang/Object; stop !                     !   >     *� *� Y� � �    "   
       #        $ %    & '  !   �     R� Y� � +� � 	� 
:� � � � Y� � :%�� � *� � W� � �    "   & 	      #   1 " 8 $ < & F ' L ) Q * #   >    R $ %     R ( )    R * +    R , -   : . /  1 ! 0 1  2    � L 3 4  & 5  !   J     *+� �    "   
    .  / #         $ %      ( )     * +   6    !   t     #*� � L+�  � +�  � M,� ���    "       3  5  6 " 7 #       7 1    # $ %   2    �  8�   9    :