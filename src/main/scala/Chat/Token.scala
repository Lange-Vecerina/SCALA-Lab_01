package Chat

enum Token:
  case // Terms
       BONJOUR, 
       QUEL,
       LE,
       PRIX,
       DE,
       COMBIEN,
       JE,
       ME,
       MON,
       SOLDE,
       SVP,
       ASSOIFFE,
       AFFAME,
       // Actions
       ETRE,
       VOULOIR,
       COMMANDER,
       CONNAITRE,
       COUTER,
       APPELLER,
       // Logic Operators
       ET,
       OU,
       // Products
       PRODUIT,
       MARQUE,
       // Util
       PSEUDO,
       NUM,
       EOL,
       UNKNOWN,
       BAD
end Token
