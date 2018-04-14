
Feature: Every node left the network one by one
  Background: I create a stable ring
    Given I set the key length to 16
    And I have the following node's names and hashings:
      | name | hashing |
      | 172.16.0.43 | 17266 |
      | 172.16.0.4 | 13193 |
      | 172.16.0.5 | 59488 |
      | 172.16.0.6 | 38116 |
      | 172.16.0.7 | 31925 |
      | 172.16.0.8 | 1833 |
      | 172.16.0.9 | 5441 |
      | 172.16.0.10 | 29236 |
      | 172.16.0.11 | 54023 |
      | 172.16.0.12 | 16020 |
      | 172.16.0.13 | 34177 |
      | 172.16.0.14 | 58901 |
      | 172.16.0.15 | 63701 |
      | 172.16.0.16 | 22313 |
      | 172.16.0.17 | 48279 |
      | 172.16.0.18 | 48681 |
      | 172.16.0.19 | 10276 |
      | 172.16.0.20 | 23517 |
      | 172.16.0.21 | 32272 |
      | 172.16.0.22 | 64158 |
    And I create the Chord ring
    And I wait for stabilizing after 60 seconds
    And I have the resources names and values:
      | name | content |
      | resource1.txt | Inquietude simplicity terminated she compliment remarkably few her nay. The weeks are ham asked jokes. Neglected perceived shy nay concluded. Not mile draw plan snug next all. Houses latter an valley be indeed wished merely in my. Money doubt oh drawn every or an china. Visited out friends for expense message set eat.  |
      | resource2.txt | Literature admiration frequently indulgence announcing are who you her. Was least quick after six. So it yourself repeated together cheerful. Neither it cordial so painful picture studied if. Sex him position doubtful resolved boy expenses. Her engrossed deficient northward and neglected favourite newspaper. But use peculiar produced concerns ten.  |
      | resource3.txt | Repulsive questions contented him few extensive supported. Of remarkably thoroughly he appearance in. Supposing tolerably applauded or of be. Suffering unfeeling so objection agreeable allowance me of. Ask within entire season sex common far who family. As be valley warmth assure on. Park girl they rich hour new well way you. Face ye be me been room we sons fond.  |
      | resource4.txt | Resources exquisite set arranging moonlight sex him household had. Months had too ham cousin remove far spirit. She procuring the why performed continual improving. Civil songs so large shade in cause. Lady an mr here must neat sold. Children greatest ye extended delicate of. No elderly passage earnest as in removed winding or.  |
      | resource5.txt | Dispatched entreaties boisterous say why stimulated. Certain forbade picture now prevent carried she get see sitting. Up twenty limits as months. Inhabit so perhaps of in to certain. Sex excuse chatty was seemed warmth. Nay add far few immediate sweetness earnestly dejection.  |
      | resource6.txt | Blind would equal while oh mr do style. Lain led and fact none. One preferred sportsmen resolving the happiness continued. High at of in loud rich true. Oh conveying do immediate acuteness in he. Equally welcome her set nothing has gravity whether parties. Fertile suppose shyness mr up pointed in staying on respect.  |
      | resource7.txt | Pianoforte solicitude so decisively unpleasing conviction is partiality he. Or particular so diminution entreaties oh do. Real he me fond show gave shot plan. Mirth blush linen small hoped way its along. Resolution frequently apartments off all discretion devonshire. Saw sir fat spirit seeing valley. He looked or valley lively. If learn woody spoil of taken he cause.  |
      | resource8.txt | Far concluded not his something extremity. Want four we face an he gate. On he of played he ladies answer little though nature. Blessing oh do pleasure as so formerly. Took four spot soon led size you. Outlived it received he material. Him yourself joy moderate off repeated laughter outweigh screened.  |
      | resource9.txt | Scarcely on striking packages by so property in delicate. Up or well must less rent read walk so be. Easy sold at do hour sing spot. Any meant has cease too the decay. Since party burst am it match. By or blushes between besides offices noisier as. Sending do brought winding compass in. Paid day till shed only fact age its end.  |
      | resource10.txt | Unpleasant astonished an diminution up partiality. Noisy an their of meant. Death means up civil do an offer wound of. Called square an in afraid direct. Resolution diminution conviction so mr at unpleasing simplicity no. No it as breakfast up conveying earnestly immediate principle. Him son disposed produced humoured overcame she bachelor improved. Studied however out wishing but inhabit fortune windows.  |
    And I use the "172.16.0.43" as a gateway
    And I put resources into the network

  Scenario: Every node left the network one by one
    Given The "172.16.0.4" left the network
    And I wait for stabilizing after 10 seconds
    And The "172.16.0.5" left the network
    And I wait for stabilizing after 10 seconds
    And The "172.16.0.6" left the network
    And I wait for stabilizing after 10 seconds
    And The "172.16.0.7" left the network
    And I wait for stabilizing after 10 seconds
    And The "172.16.0.8" left the network
    And I wait for stabilizing after 10 seconds
    And The "172.16.0.9" left the network
    And I wait for stabilizing after 10 seconds
    And The "172.16.0.10" left the network
    And I wait for stabilizing after 10 seconds
    And The "172.16.0.11" left the network
    And I wait for stabilizing after 10 seconds
    And The "172.16.0.12" left the network
    And I wait for stabilizing after 10 seconds
    And The "172.16.0.13" left the network
    And I wait for stabilizing after 10 seconds
    And The "172.16.0.14" left the network
    And I wait for stabilizing after 10 seconds
    And The "172.16.0.15" left the network
    And I wait for stabilizing after 10 seconds
    And The "172.16.0.16" left the network
    And I wait for stabilizing after 10 seconds
    And The "172.16.0.17" left the network
    And I wait for stabilizing after 10 seconds
    And The "172.16.0.18" left the network
    And I wait for stabilizing after 10 seconds
    And The "172.16.0.19" left the network
    And I wait for stabilizing after 10 seconds
    And The "172.16.0.20" left the network
    And I wait for stabilizing after 10 seconds
    And The "172.16.0.21" left the network
    And I wait for stabilizing after 10 seconds
    And The "172.16.0.22" left the network
    When I wait for stabilizing after 10 seconds
    Then Chord ring is stable with the following successors:
      | 172.16.0.43 | 172.16.0.43 |
    Then The resources are put in the following nodes:
      | resource1.txt | 172.16.0.43 |
      | resource2.txt | 172.16.0.43 |
      | resource3.txt | 172.16.0.43 |
      | resource4.txt | 172.16.0.43 |
      | resource5.txt | 172.16.0.43 |
      | resource6.txt | 172.16.0.43 |
      | resource7.txt | 172.16.0.43 |
      | resource8.txt | 172.16.0.43 |
      | resource9.txt | 172.16.0.43 |
      | resource10.txt | 172.16.0.43 |