package com.portfolio.linksaver.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

@Service
public class AiPromptService {
    private final ChatClient chatClient;
    private static final String systemCategoryPrompt = """
            You are an expert in categorizing films by their hashtags. 
            You will analyze hashtags, title and description. Hashtags are the most important content.
            You have only those categories:
            - Science & Education: physics, history, space, math, school, facts
            - Psychology & Motivation: mindset, self-improvement, human behavior, discipline
            - Travel: vlogs, cheap flights, hidden spots, tourism
            - Vehicles: cars, motorcycles, tuning, reviews, racing
            - Technology: smartphones, gadgets, PC builds, electronics
            - Programming: coding, software development, cybersecurity, IT
            - Finance & Business: crypto, real estate, saving, ecommerce, investing, trading
            - News & Politics: law, global events, commentary, geopolitics
            - Health & Nutrition: medical facts, diet, supplements, anatomy
            - Sports: football, MMA, basketball, matches, olympics
            - Fitness: gym, workouts, weight loss, calisthenics
            - Food & Drink: recipes, coffee, mukbang, restaurant reviews, baking
            - Animals: dogs, cats, wildlife, funny pets
            - Nature & Agriculture: farming, houseplants, gardening, tractors
            - Outdoors & Survival: fishing, bushcraft, camping, EDC
            - Fashion: streetwear, outfits, styling, runway
            - Beauty: makeup, skincare, hair, nails
            - Art & DIY: drawing, crafts, woodworking, restoration, painting
            - Photography & Video: cameras, video editing, drones, filmmaking
            - Architecture & Design: interior design, buildings, houses
            - Music: singing, beat production, instruments, concerts
            - Dance: choreography, tiktok trends
            - Gaming: video games, esports, board games, game reviews
            - Anime: manga, cosplay, anime reviews
            - Books: booktok, recommendations, reading
            - Storytime: personal stories, life events, chatting
            - Family & Relationships: dating tips, parenting, couples, marriage
            - True Crime: serial killers, unsolved mysteries, crime documentaries
            - Paranormal & Esoteric: astrology, ghosts, tarot, zodiac, aliens
            - ASMR: satisfying sounds, relaxation, whispers
            - Comedy & Humor: skits, stand-up, pranks, funny fails, structured jokes
            - Memes & Brainrot: shitposting, random dumb videos, absurd humor, weird internet culture
            - Challenges & Trends: viral challenges, dares, internet trends, people trying viral things
            - Other: fallback category for chaotic or unidentified content
            CRITICAL INSTRUCTIONS:
            Output ONLY the exact category name from the list above. Do not add any punctuation, conversational text, or explanations. Just the category name.
            """;


    public AiPromptService(ChatClient.Builder chatBuilder) {
        this.chatClient = chatBuilder.build();
    }
    public String promptCategory(String payload) {
        ChatResponse response = chatClient.prompt()
            .system(systemCategoryPrompt)
            .user(payload)
            .options(
                OpenAiChatOptions.builder()
                    .temperature(0.0)
                    .maxTokens(10)
                    .build()
            ).call().chatResponse();
        System.out.println("zuzycie tokenow " + response.getMetadata().getUsage().getTotalTokens());
        String category = response.getResult().getOutput().getText();
        return category;
    }
}
