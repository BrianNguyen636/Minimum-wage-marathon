import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Facts {
    private static final List<String> facts = Arrays.asList(
            "Our minimum wage is $13.50 an hour. The average living wage for a single adult is $18.56 at 40 hrs a week.",
            "In 2015 the average student loan debt was $24,600. The annual minimum wage at that time was just under $20,000.",
            "The required annual income for a single adult is around $38,000 before tax. WA minimum wage makes $29,000.",
            "Housing for 1 adult is $17,000 a year, more than 50% of the minimum wage income.",
            "Living wage for two working parents to support two children is $23.47 per parent.",
            "One Parent, one child living wage is $35.19, nearly triple the minimum wage.",
            "Average college tuition is around $10,000. A bit over a third of the annual minimum wage."
    );
    public static String fact() {
        Random rand = new Random();
        int x = rand.nextInt(facts.size());
        return facts.get(x);
    }

}
