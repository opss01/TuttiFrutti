package com.example.tuttifrutti.gameutils;
import android.app.Activity;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.games.Games;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.database.DatabaseReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArraySet;

public class GameUtils {
    private static final String TAG = "GameUtils";
    private static FirebaseFirestore db = null;
    static List<String> fieldValues = new ArrayList<>();

    private final static char firstLetterOfAlphabet = 'A';
    private final static int numLettersInAlphabet = 26;

    public final static String CURRENT_PLAYER_KEY="currentPlayer";
    public final static String OTHER_PLAYER_KEY="otherPlayer";
    public final static String CURRENT_PLAYER_SCORE_KEY="currentPlayerScore";
    public final static String OTHER_PLAYER_SCORE_KEY="otherPlayerScore";
    private static FirebaseAuth auth = FirebaseAuth.getInstance();
//    public static void setFirebaseCredentials(FirebaseAuth authgood){
//        auth = authgood;
//        return;
//    }
    public static boolean isLoggedIn(Activity activity) {
        //COMPLETED_TODO: Return true if user is logged in
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(activity);
        if (account == null || !GoogleSignIn.hasPermissions(account, Games.SCOPE_GAMES_LITE)) {
            return false;
        } else {
            return true;
        }
    }

    public static Object dbSetup () {
        // Access a Cloud Firestore instance from your Activity
        // [START get_firestore_instance]
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // [END get_firestore_instance]

        // [START set_firestore_settings]
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        // [END set_firestore_settings]
        return db;

    }
    static class Categories {
        String category;
        String value;

        public Categories() {
        }
        public Categories(String c, String v)
        {
            category = c;
            value = v;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getVal() {
            return value;
        }

        public void setVal(String values) {
            this.value = values;
        }
    }

    public static Map<String, String> getCategoryValuesFromDB(String cat)
    {


        Map<String,String> categoryValues = new HashMap<>();
        //Firebase
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
//        final String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        dbRef.child("categories").child("").addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot catDataSnapshot : dataSnapshot.getChildren()) {

                            Categories cat1 = catDataSnapshot.getValue(Categories.class);
                            Log.d(TAG,"CAT" + cat1.getCategory() + " Value " + cat1.getVal());
                            if(cat1.getCategory().toString().contentEquals(cat.toLowerCase()))
                            {categoryValues.put(cat1.getCategory(),cat1.getVal());
                                Log.d(TAG,"issue" + cat1.getVal());}
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "Error trying to get classified ad for update " +
                                ""+databaseError);

                    }
                });
        return categoryValues;

    }

    public static int getScoreForCategory (String category, String responses, String letter) {
        //TODO: Implement a method that for a given category, validates responses in DB.
        // method should send back the number of correct responses.
//        Map<String,String> cat = getCategoryValuesFromDB(category);
        int score = 0;
        Log.d(TAG,"Responses" + responses);
//        ArrayList<String> colorsls = new ArrayList<String>();
//        colorsls.add("amber");
//        colorsls.add("amethyst");
//        colorsls.add("apricot");
        ArrayList<String> colorsls = new ArrayList<String>();
        Collections.addAll(colorsls, "amber","amethyst","apricot","aquamarine","azure","baby blue","beige","black","blue","blue-green",
                "blue-violet","blush","bronze","brown","burgundy","byzantium","carmine","cerise","cerulean","champagne","chartreuse green","chocolate",
                "cobalt blue","coffee","copper","coral","crimson","cyan","desert sand","electric blue","emerald","erin","gold","gray","green","harlequin",
                "indigo","ivory","jade","jungle green","lavender","lemon","lilac","lime","magenta","magenta rose","maroon","mauve","navy blue","ochre",
                "olive","orange","orange-red","orchid","peach","pear","periwinkle","persian blue","pink","plum","prussian blue","puce","purple",
                "raspberry","red","red-violet","rose","ruby","salmon","sangria","sapphire","scarlet","silver","slate gray","spring bud","spring green",
                "tan","taupe","teal","turquoise","ultramarine","violet","viridian","white","yellow");
        ArrayList<String> animalsls = new ArrayList<String>();
        Collections.addAll(colorsls, "cat","dog","donkey","goat","guinea pig","horse","pig","rabbit","water buffalo","alpaca","american buffalo",
                "robin","anaconda","angelfish","anglerfish","ant","anteater","antelope","antlion","ape","aphid","arabian leopard","arctic fox",
                "arctic wolf","armadillo","arrow crab","asp","ass","baboon","badger","bald eagle","bandicoot","barnacle","barracuda","basilisk","bass",
                "bat","beaked whale","bear","beaver","bedbug","bee","beetle","bird","bison","blackbird","black panther","black widow spider",
                "blue bird","blue jay","blue whale","boa","boar","bobcat","bobolink","bonobo","box jellyfish","bovid","buffalo","bug","butterfly","buzzard",
                "camel","canid","cape buffalo","capybara","cardinal","caribou","carp","cat","catshark","caterpillar","catfish","cattle","centipede",
                "cephalopod","chameleon","cheetah","chickadee","chicken","chimpanzee","chinchilla","chipmunk","clam","clownfish","cobra","cockroach",
                "cod","condor","constrictor","coral","cougar","cow","coyote","crab","crane","crane fly","crawdad","crayfish","cricket","crocodile","crow",
                "cuckoo","cicada","damselfly","deer","dingo","dinosaur","dolphin","donkey","dormouse","dove","dragonfly","dragon",
                "duck","dung beetle","eagle","earthworm","earwig","echidna","eel","egret","elephant","elephant seal","elk","emu","english pointer",
                "ermine","falcon","ferret","finch","firefly","fish","flamingo","flea","fly","flyingfish","fowl","fox","frog","fruit bat","gamefowl",
                "galliform","gazelle","gecko","gerbil","giant panda","giant squid","gibbon","gila monster","giraffe","goat","goldfish",
                "goose","gopher","gorilla","grasshopper","great blue heron","great white shark","grizzly bear","ground shark","ground sloth",
                "grouse","guan","guanaco","guineafowl","gull","guppy","haddock","halibut","hammerhead shark","hamster",
                "hare","harrier","hawk","hedgehog","hermit crab","heron","herring","hippopotamus","hookworm","hornet","horse","hoverfly",
                "hummingbird","humpback whale","hyena","iguana","impala","irukandji jellyfish","jackal","jaguar","jay","jellyfish","junglefowl",
                "kangaroo","kangaroo mouse","kangaroo rat","kingfisher","kite","kiwi","koala","koi","komodo dragon","krill","ladybug","lamprey",
                "landfowl","land snail","lark","leech","lemming","lemur","leopard","leopon","limpet","lion","lizard","llama","lobster","locust",
                "loon","louse","lungfish","lynx","macaw","mackerel","magpie","mammal","manatee","mandrill","manta ray","marlin","marmoset","marmot",
                "marsupial","marten","mastodon","meadowlark","meerkat","mink","minnow","mite","mockingbird","mole","mollusk","mongoose","monitor lizard",
                "monkey","moose","mosquito","moth","mountain goat","mouse","mule","muskox","narwhal","newt","new world quail","nightingale","ocelot",
                "octopus","old world quail","opossum","orangutan","orca","ostrich","otter","owl","ox","panda","panther","panthera hybrid","parakeet",
                "parrot","parrotfish","partridge","peacock","peafowl","pelican","penguin","perch","peregrine falcon","pheasant","pig","pigeon",
                "pike","pilot whale","pinniped","piranha","planarian","platypus","polar bear","pony","porcupine","porpoise",
                "possum","prairie dog","prawn","praying mantis","primate","ptarmigan","puffin","puma","python","quail","quelea","quokka","rabbit",
                "raccoon","rainbow trout","rat","rattlesnake","raven","ray","red panda","reindeer","reptile","rhinoceros",
                "right whale","roadrunner","rodent","rook","rooster","roundworm","saber-toothed cat","sailfish","salamander","salmon","sawfish",
                "scale insect","scallop","scorpion","seahorse","sea lion","sea slug","sea snail","shark","sheep","shrew","shrimp","silkworm",
                "silverfish","skink","skunk","sloth","slug","smelt","snail","snake","snipe","snow leopard","sockeye salmon","sole","sparrow",
                "sperm whale","spider","spider monkey","spoonbill","squid","squirrel","starfish","star-nosed mole","steelhead trout","stingray","stoat",
                "stork","sturgeon","sugar glider","swallow","swan","swift","swordfish","swordtail","tahr","takin","tapir","tarantula","tarsier",
                "tasmanian devil","termite","tern","thrush","tick","tiger","tiger shark","tiglon","toad","tortoise","toucan","trapdoor spider","tree frog",
                "trout","tuna","turkey","turtle","tyrannosaurus","urial","vampire bat","vampire squid","vicuna","viper","vole","vulture","wallaby",
                "walrus","wasp","warbler","water boa","weasel","whale","whippet","whitefish","whooping crane","wildcat","wildebeest",
                "wildfowl","wolf","wolverine","wombat","woodpecker","worm","wren","xerinae","x-ray fish","yak","yellow perch","zebra","zebra finch");

        String lines[] = responses.toLowerCase().split("\\r?\\n");
        for (int i=0;i<lines.length;i++)
        {
            if (lines[i].charAt(0) == letter.toLowerCase().charAt(0))
            {Log.d(TAG,"in LOOP " + category.toLowerCase().trim().toString() + "999" + lines[i].trim().toString());
                if (category.toLowerCase().trim().toString().contentEquals("colors") && colorsls.contains(lines[i].trim().toString()))
                {Log.d(TAG,"String in loop: " + lines[i]);
            score += 3;}
            }
        }
        return score;
    }

    /* TODO: DELETE */

/* getSomeCategories
Input: A list of unique(preferably) categories, the number of categories you need.
Output: after shuffling the list it will pick the first ones in the list up to the number in the
second input.

Note: There is some checks in case the list is empty etc.
 */
    /*
    public static List<String> getSomeCategories ( List<String> allCats, int n ) {
        if (allCats.size() < n && allCats.size() != 0 ) { n = allCats.size();}
        else if (allCats.size() == 0) { return allCats;}
        List<String> fList = new LinkedList<String>(allCats);
        Collections.shuffle(fList);
        return fList.subList(0, n);
    }

    public interface MyCallback {
        void onCallback(Categories value);
    }

    public static void readData(MyCallback myCallback) {
        FirebaseDatabase.getInstance(); //.setPersistenceEnabled(true);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        final Query query =dbRef.child("categories").child("");
//        query.keepSynced(true);
        dbRef.child("categories").child("").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot catDataSnapshot : dataSnapshot.getChildren()) {
                    Categories cat1 = catDataSnapshot.getValue(Categories.class);

                    if (holdValues.isEmpty())
                    {addToHoldValues(cat1.getCategory());
                        myCallback.onCallback(cat1);}
                    if (!holdValues.contains(cat1.getCategory())) {

                        addToHoldValues(cat1.getCategory());

                        Log.d(TAG,"CATEGORY " + cat1.getCategory());
                        myCallback.onCallback(cat1);
                    }

//                    holdValues.add(cat1.getCategory());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Error trying to get classified ad for update " +
                        ""+databaseError);

            }
        });

    }
    */

    public static ArrayList<String> holdValues = new ArrayList<String> ();
    /* Returns n number of categories based on whatever is in the database */
    public static List<String> getRandomCategories(int n) {
        //TODO: Implement getRandomCategories
        //Firebase


//        readData(new MyCallback() {
//            @Override
//            public void onCallback(Categories value) {
//                Log.d("TAG", value.getCategory());
//                addToHoldValues(value.getCategory());
//            }
//        });

//                dbRef.child("categories").child("").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                for (DataSnapshot catDataSnapshot : dataSnapshot.getChildren()) {
//                    Categories cat1 = catDataSnapshot.getValue(Categories.class);
//                    if (holdValues.isEmpty())
//                    {addToHoldValues(cat1.getCategory());}
//                    if (!holdValues.contains(cat1.getCategory())) {
//
//                        addToHoldValues(cat1.getCategory());
//
//                        Log.d(TAG,"CATEGORY " + cat1.getCategory());
//                    }
//
////                    holdValues.add(cat1.getCategory());
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d(TAG, "Error trying to get classified ad for update " +
//                        ""+databaseError);
//
//            }
//        });

        holdValues.add("Colors");
        holdValues.add("Animals");

        return (holdValues);
    }

    private static void addToHoldValues(String category) {
        holdValues.add(category);
    }

    public static char randomLetter (){
        Random r = new Random();
        int nextRand = r.nextInt(numLettersInAlphabet);
        return (char) (firstLetterOfAlphabet + nextRand);
        //return sequence.remove( nextRand );
    }
}
