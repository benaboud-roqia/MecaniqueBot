package com.todolist.mechanique.webhook;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

@RestController
public class WebhookController {

    private List<JSONObject> materiaux;

    public WebhookController() {
        try {
            String json = new String(Files.readAllBytes(Paths.get("materiaux.json")));
            org.json.JSONArray arr = new org.json.JSONArray(json);
            materiaux = new java.util.ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                materiaux.add(arr.getJSONObject(i));
            }
        } catch (Exception e) {
            materiaux = java.util.Collections.emptyList();
        }
    }

    @PostMapping("/webhook")
    public String webhook(@RequestBody String payload) {
        JSONObject request = new JSONObject(payload);

        // Récupérer l'intention et les paramètres extraits par Dialogflow
        String intent = request.optJSONObject("queryResult") != null
                ? request.getJSONObject("queryResult").optJSONObject("intent").optString("displayName", "")
                : "";
        JSONObject params = request.optJSONObject("queryResult") != null
                ? request.getJSONObject("queryResult").optJSONObject("parameters")
                : new JSONObject();
        String domaine = request.optString("domaine", "Général");
        String query = request.optString("query", "");

        // Message d'accueil ou d'aide contextuelle selon le domaine
        if (query.trim().isEmpty() || query.equalsIgnoreCase("aide") || query.equalsIgnoreCase("help")) {
            String aide = getAideDomaine(domaine);
            JSONObject response = new JSONObject();
            response.put("fulfillmentText", aide);
            response.put("reply", aide);
            return response.toString();
        }

        String reply;
        switch (domaine) {
            case "Résistance des matériaux":
                reply = handleResistanceMateriaux(intent, params, query);
                break;
            case "Fluides":
                reply = handleFluides(intent, params, query);
                break;
            case "Thermodynamique":
                reply = handleThermodynamique(intent, params, query);
                break;
            case "Matériaux":
                reply = handleMateriaux(intent, params, query);
                break;
            default:
                reply = handleGeneral(intent, params, query);
        }

        JSONObject response = new JSONObject();
        response.put("fulfillmentText", reply);
        response.put("reply", reply);
        return response.toString();
    }

    private String getAideDomaine(String domaine) {
        switch (domaine) {
            case "Résistance des matériaux":
                return "Bienvenue dans le domaine Résistance des matériaux !\nExemples :\n- Calcule la contrainte pour 5000N sur 200mm²\n- Flèche d'une poutre IPE200 de 3m avec 10kN au centre";
            case "Fluides":
                return "Bienvenue dans le domaine Fluides !\nExemples :\n- Débit d'eau dans un tuyau DN50 à 3m/s\n- Pertes de charge sur 100m de tuyau DN50 à 2m/s";
            case "Thermodynamique":
                return "Bienvenue dans le domaine Thermodynamique !\nExemples :\n- Puissance thermique pour 10kg d'eau, ΔT = 20K en 600s\n- Calcul du rendement d'un cycle de Carnot";
            case "Matériaux":
                return "Bienvenue dans le domaine Matériaux !\nExemples :\n- Module d'Young de l'acier\n- Densité du béton C25/30";
            default:
                return "Bienvenue sur MecaBot !\nPosez une question ou choisissez un domaine.\nExemples :\n- Calcule la contrainte pour 5000N sur 200mm²\n- Convertir 25 bar en psi\n- Module d'Young de l'acier";
        }
    }

    // Gestion par domaine
    private String handleResistanceMateriaux(String intent, JSONObject params, String query) {
        if (intent.equals("CalculContrainte")) {
            double force = params.optDouble("force", -1);
            double surface = params.optDouble("surface", -1);
            String uniteForce = params.optString("unite_force", "N");
            String uniteSurface = params.optString("unite_surface", "mm²");
            if (force > 0 && surface > 0) {
                double contrainte = force / surface;
                return String.format("La contrainte est σ = %.3f N/mm² (F = %.2f %s, S = %.2f %s)", contrainte, force, uniteForce, surface, uniteSurface);
            } else {
                return "Merci de préciser la force et la surface.";
            }
        }
        return "Demande non reconnue en Résistance des matériaux.";
    }

    private String handleFluides(String intent, JSONObject params, String query) {
        if (intent.equals("CalculDebit")) {
            double vitesse = params.optDouble("vitesse", -1);
            double diametre = params.optDouble("diametre", -1);
            String uniteDiametre = params.optString("unite_diametre", "mm");
            if (vitesse > 0 && diametre > 0) {
                double d_m = UnitesUtils.convertirLongueur(diametre, uniteDiametre);
                double A = Math.PI * Math.pow(d_m / 2, 2);
                double debit = vitesse * A;
                return String.format("Le débit volumique est Q = %.4f m³/s (v = %.2f m/s, D = %.2f %s)", debit, vitesse, diametre, uniteDiametre);
            } else {
                return "Merci de préciser la vitesse et le diamètre.";
            }
        }
        if (intent.equals("CalculPerteCharge")) {
            double longueur = params.optDouble("longueur", -1);
            double diametrePC = params.optDouble("diametre", -1);
            double vitessePC = params.optDouble("vitesse", -1);
            double densite = params.optDouble("densite", 1000);
            double f = params.optDouble("coefficient_frottement", 0.02);
            String uniteLongueur = params.optString("unite_longueur", "m");
            String uniteDiametrePC = params.optString("unite_diametre", "mm");
            if (longueur > 0 && diametrePC > 0 && vitessePC > 0) {
                double L = UnitesUtils.convertirLongueur(longueur, uniteLongueur);
                double D = UnitesUtils.convertirLongueur(diametrePC, uniteDiametrePC);
                double deltaP = f * (L / D) * (densite * Math.pow(vitessePC, 2) / 2);
                return String.format("La perte de charge est ΔP = %.2f Pa (L = %.2f %s, D = %.2f %s, v = %.2f m/s, ρ = %.1f kg/m³, f = %.3f)", deltaP, longueur, uniteLongueur, diametrePC, uniteDiametrePC, vitessePC, densite, f);
            } else {
                return "Merci de préciser la longueur, le diamètre et la vitesse.";
            }
        }
        return "Demande non reconnue en Fluides.";
    }

    private String handleThermodynamique(String intent, JSONObject params, String query) {
        if (intent.equals("CalculPuissanceThermique")) {
            double masse = params.optDouble("masse", -1);
            double capacite = params.optDouble("capacite_thermique", 4180);
            double deltaT = params.optDouble("deltaT", -1);
            double temps = params.optDouble("temps", 1);
            if (masse > 0 && deltaT > 0 && temps > 0) {
                double puissance = masse * capacite * deltaT / temps;
                return String.format("La puissance thermique est P = %.2f W (m = %.2f kg, c = %.1f J/kg.K, ΔT = %.2f K, t = %.2f s)", puissance, masse, capacite, deltaT, temps);
            } else {
                return "Merci de préciser la masse, la capacité thermique, le delta T et le temps.";
            }
        }
        return "Demande non reconnue en Thermodynamique.";
    }

    private String handleMateriaux(String intent, JSONObject params, String query) {
        String materiau = params.optString("materiau", "");
        return infoMateriau(materiau);
    }

    private String handleGeneral(String intent, JSONObject params, String query) {
        if (intent.equals("ConversionUnite")) {
            double valeur = params.optDouble("valeur", -1);
            String uniteDepart = params.optString("unite_depart", "");
            String uniteArrivee = params.optString("unite_arrivee", "");
            return conversionUnites(valeur, uniteDepart, uniteArrivee);
        }
        if (intent.equals("InfoMateriau")) {
            String materiau = params.optString("materiau", "");
            return infoMateriau(materiau);
        }
        // Recherche enrichie si aucune réponse locale
        String wiki = getWikipediaSummary(query);
        if (wiki != null && !wiki.isEmpty() && !wiki.startsWith("Erreur")) return wiki;
        String openai = getOpenAICompletion(query);
        if (openai != null && !openai.isEmpty() && !openai.startsWith("Erreur")) return openai;
        String bing = getBingSearchResult(query);
        if (bing != null && !bing.isEmpty() && !bing.startsWith("Erreur")) return bing;
        String toolbox = getEngineeringToolboxLink(query);
        String matweb = getMatWebLink(query);
        return "Pour plus d'informations techniques :\nEngineering Toolbox : " + toolbox + "\nMatWeb : " + matweb;
    }

    private String infoMateriau(String materiau) {
        if (materiau == null || materiau.isEmpty()) return "Merci de préciser le matériau.";
        for (JSONObject obj : materiaux) {
            if (obj.getString("nom").equalsIgnoreCase(materiau)) {
                return String.format(
                        "%s %s :\nModule d'Young = %s\nLimite élastique = %s\nRésistance ultime = %s\nDensité = %s\nPoisson = %s\nConductivité = %s",
                        obj.optString("nom", ""),
                        obj.optString("type", ""),
                        obj.optString("module_young", "?"),
                        obj.optString("limite_elastique", "?"),
                        obj.optString("resistance_ultime", "?"),
                        obj.optString("densite", "?"),
                        obj.optString("poisson", "?"),
                        obj.optString("conductivite", "?")
                );
            }
        }
        return "Matériau inconnu ou non supporté.";
    }

    private String conversionUnites(double valeur, String depart, String arrivee) {
        if (depart.equals("bar") && arrivee.equals("psi")) {
            return String.format("%.2f bar = %.2f psi", valeur, valeur * 14.5038);
        }
        if (depart.equals("psi") && arrivee.equals("bar")) {
            return String.format("%.2f psi = %.2f bar", valeur, valeur / 14.5038);
        }
        return "Conversion non supportée pour l'instant.";
    }

    // --- Wikipedia ---
    public String getWikipediaSummary(String query) {
        try {
            String apiUrl = "https://fr.wikipedia.org/api/rest_v1/page/summary/" +
                java.net.URLEncoder.encode(query, "UTF-8");
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            Scanner in = new Scanner(conn.getInputStream());
            StringBuilder response = new StringBuilder();
            while (in.hasNext()) response.append(in.nextLine());
            in.close();
            JSONObject json = new JSONObject(response.toString());
            return json.optString("extract", "Aucune information trouvée sur Wikipedia.");
        } catch (Exception e) {
            return "Erreur lors de la recherche Wikipedia.";
        }
    }

    // --- OpenAI ---
    public String getOpenAICompletion(String prompt) {
        try {
            String apiKey = "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";
            URL url = new URL("https://api.openai.com/v1/chat/completions");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", prompt);

            JSONObject body = new JSONObject();
            body.put("model", "gpt-3.5-turbo");
            body.put("messages", new org.json.JSONArray().put(message));
            body.put("max_tokens", 200);

            conn.getOutputStream().write(body.toString().getBytes("UTF-8"));

            Scanner in = new Scanner(conn.getInputStream());
            StringBuilder response = new StringBuilder();
            while (in.hasNext()) response.append(in.nextLine());
            in.close();

            JSONObject json = new JSONObject(response.toString());
            return json.getJSONArray("choices")
                       .getJSONObject(0)
                       .getJSONObject("message")
                       .getString("content");
        } catch (Exception e) {
            return "Erreur lors de la requête OpenAI.";
        }
    }

    // --- Bing Web Search ---
    public String getBingSearchResult(String query) {
        try {
            String apiKey = "TA_CLE_BING"; // Mets ici ta clé Bing
            String apiUrl = "https://api.bing.microsoft.com/v7.0/search?q=" +
                java.net.URLEncoder.encode(query, "UTF-8");
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Ocp-Apim-Subscription-Key", apiKey);
            Scanner in = new Scanner(conn.getInputStream());
            StringBuilder response = new StringBuilder();
            while (in.hasNext()) response.append(in.nextLine());
            in.close();
            JSONObject json = new JSONObject(response.toString());
            if (json.has("webPages")) {
                JSONObject first = json.getJSONObject("webPages")
                                      .getJSONArray("value").getJSONObject(0);
                return first.getString("name") + "\n" + first.getString("url");
            }
            return "Aucun résultat Bing trouvé.";
        } catch (Exception e) {
            return "Erreur lors de la recherche Bing.";
        }
    }

    public String getEngineeringToolboxLink(String query) {
        try {
            return "https://www.engineeringtoolbox.com/search.html?q=" +
                java.net.URLEncoder.encode(query, "UTF-8");
        } catch (Exception e) {
            return "https://www.engineeringtoolbox.com/";
        }
    }

    public String getMatWebLink(String query) {
        try {
            return "https://www.matweb.com/search/DataSheet.aspx?MatGUID=&Search=" +
                java.net.URLEncoder.encode(query, "UTF-8");
        } catch (Exception e) {
            return "https://www.matweb.com/";
        }
    }
} 